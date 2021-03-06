/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.events.extensions.bscenarios;

import io.novaordis.clad.application.ApplicationRuntime;
import io.novaordis.clad.command.CommandBase;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.option.BooleanOption;
import io.novaordis.clad.option.Option;
import io.novaordis.events.api.event.EndOfStreamEvent;
import io.novaordis.events.api.event.Event;
import io.novaordis.events.api.event.FaultEvent;
import io.novaordis.events.api.event.FaultType;
import io.novaordis.events.clad.EventsApplicationRuntime;
import io.novaordis.events.core.EndOfStreamListener;
import io.novaordis.events.core.Terminator;
import io.novaordis.events.extensions.bscenarios.stats.BusinessScenarioStateStatistics;
import io.novaordis.events.extensions.bscenarios.stats.BusinessScenarioStatistics;
import io.novaordis.events.extensions.bscenarios.stats.FaultStatistics;
import io.novaordis.events.httpd.HttpdFormatString;
import io.novaordis.events.httpd.HttpEvent;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.time.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 3/1/16
 */
public class BusinessScenarioCommand extends CommandBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(BusinessScenarioCommand.class);

    private static final BooleanOption STATS_OPTION = new BooleanOption("stats");

    // Static ----------------------------------------------------------------------------------------------------------

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(HttpdFormatString.TIMESTAMP_FORMAT_STRING);

    public static String formatTimestamp(Timestamp timestamp) {

        //
        // currently we use the standard httpd timestamp format, but TODO in the future we must generalize this and
        // be able to use the same time format used in the input log - to ease searching.
        //
        // TODO implement a better concurrent access than synchronization
        //

        if (timestamp == null) {
            return "-";
        }

        synchronized (TIMESTAMP_FORMAT) {

            return timestamp.format(TIMESTAMP_FORMAT);
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private BlockingQueue<Event> terminatorQueue;

    private HttpSessionFactory httpSessionFactory;

    // JSESSIONID - HttpSession instance
    private Map<String, HttpSession> sessions;

    private boolean ignoreFaults;
    private boolean statsOnly;

    private long httpEventCount;

    // Constructors ----------------------------------------------------------------------------------------------------

    public BusinessScenarioCommand() {

        terminatorQueue = new ArrayBlockingQueue<>(100);
        sessions = new HashMap<>();
        //noinspection Convert2Lambda,Anonymous2MethodRef
        httpSessionFactory = new HttpSessionFactory() {
            @Override
            public HttpSession create() {
                return new HttpSession();
            }
        };

        ignoreFaults = false;
        statsOnly = false;

        log.debug(this + " created");
    }

    // CommandBase overrides -------------------------------------------------------------------------------------------

    @Override
    public Set<Option> optionalOptions() {

        return Collections.singleton(STATS_OPTION);
    }

    /**
     * Set our local variables
     */
    @Override
    public void configure(int from, List<String> commandLineArguments) throws Exception {

        super.configure(from, commandLineArguments);

        BooleanOption o = (BooleanOption)getOption(STATS_OPTION);

        if (o != null) {
            statsOnly = o.getValue();
        }

        log.debug(this + "'s stats set to " + statsOnly);
    }

    @Override
    public void execute(ApplicationRuntime r) throws Exception {

        EventsApplicationRuntime runtime = (EventsApplicationRuntime)r;

        //
        // transfer interesting global options values
        //

        Configuration configuration = r.getConfiguration();

        Option ignoreFaultsOption = configuration.getGlobalOption(EventsApplicationRuntime.IGNORE_FAULTS_OPTION);
        this.ignoreFaults = ignoreFaultsOption != null && ((BooleanOption)ignoreFaultsOption).getValue();
        log.debug(this + "'s ignoreFaults set to " + ignoreFaults);

        Terminator terminator = runtime.getTerminator();

        final CountDownLatch rendezVous = new CountDownLatch(1);

        if (terminator != null) {

            terminator.setInputQueue(terminatorQueue);

            // the output format specification is encapsulated in BusinessScenarioOutputFormatter
            BusinessScenarioOutputFormatter bsof = new BusinessScenarioOutputFormatter();

            //
            // generate headers
            //
            bsof.getCSVFormatter().setHeaderOn();

            terminator.setConversionLogic(bsof);

            //noinspection Convert2Lambda,Anonymous2MethodRef
            terminator.addEndOfStreamListener(new EndOfStreamListener() {
                @Override
                public void eventStreamEnded() {
                    rendezVous.countDown();
                }
            });
        }

        runtime.start();

        BlockingQueue<Event> httpRequestQueue = runtime.getLastEventProcessor().getOutputQueue();

        boolean incomingStreamOpen = true;

        while (incomingStreamOpen) {

            Event event = httpRequestQueue.take();

            if (event == null || event instanceof EndOfStreamEvent) {
                incomingStreamOpen = false;
            }

            List<Event> outgoing = process(event);
            handleOutgoing(outgoing);
        }

        if (statsOnly) {
            displayStatistics();
        }
        else {

            //
            // wait until terminator finishes its queue
            //
            rendezVous.await();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * This method only exists to allow unit testing. If used, it overwrites the default factory.
     */
    public void setHttpSessionFactory(HttpSessionFactory httpSessionFactory) {
        this.httpSessionFactory = httpSessionFactory;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @param incoming usually a civilian, but it can also be null or EndOfStreamEvent, so the method must be able
     *                 to handle that.
     */
    List<Event> process (Event incoming) throws UserErrorException {

        if (incoming == null || incoming instanceof EndOfStreamEvent) {

            //
            // handle end-of-stream, cleanup whatever in-flight state we might have when we detect the end of the input
            // stream; iterate over the session list and forcibly close the active scenarios
            //

            List<Event> outgoing = new ArrayList<>();

            for(HttpSession s: sessions.values()) {

                BusinessScenario bs = s.getCurrentBusinessScenario();
                if (bs.isNew()) {
                    continue;
                }
                bs.close();
                outgoing.add(bs.toEvent());
            }

            outgoing.add(new EndOfStreamEvent());
            return outgoing;
        }

        if (incoming instanceof FaultEvent) {

            return Collections.singletonList(incoming);
        }

        if (!(incoming instanceof HttpEvent)) {

            //
            // this is a programming error, stop processing right away
            //
            throw new IllegalArgumentException(this + " got an " + incoming + " while it is only expecting HttpEvents");

        }

        return processHttpEvent((HttpEvent) incoming);
    }

    /**
     * The method locates the session for the specific request, and then delegates further processing to the session.
     *
     * @return a list containing BusinessScenarioEvents and/or FaultEvent or an empty list if we're in mid-flight while
     * processing a scenario.
     *
     * @exception UserErrorException if the lower layers encountered a problem that stops us from processing the
     *  event stream (most likely because trying to produce further results won't make sense). In this case, the process
     *  must exit with a user-readable error.
     */
    List<Event> processHttpEvent(HttpEvent event) throws UserErrorException {

        httpEventCount ++;

        String jSessionId = event.getCookie(HttpEvent.JSESSIONID_COOKIE_KEY);

        if (jSessionId == null) {
            return Collections.singletonList(new FaultEvent(
                    BusinessScenarioFaultType.NO_JSESSIONID_COOKIE,
                    "HTTP request " + event + " does not carry a \"" + HttpEvent.JSESSIONID_COOKIE_KEY + "\" cookie"));
        }

        HttpSession s = sessions.get(jSessionId);

        if (s == null) {
            s = httpSessionFactory.create();
            s.setJSessionId(jSessionId);
            sessions.put(jSessionId, s);
            log.debug(s + " identified");
        }

        return s.process(event);
    }

    void handleOutgoing(List<Event> outgoing) throws InterruptedException{

        if (statsOnly) {

            updateStatistics(outgoing);
        }
        else {

            //
            // send outgoing events downstream
            //

            for (Event o : outgoing) {

                if (o == null) {
                    continue;
                }

                if (ignoreFaults && o instanceof FaultEvent) {

                    //
                    // we are configured to not display faults
                    //

                    log.debug("ignoring " + o);
                    continue;
                }

                terminatorQueue.put(o);
            }
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private long otherEventsCount;
    private BusinessScenarioStatistics bsStats = new BusinessScenarioStatistics();
    private FaultStatistics faultStats = new FaultStatistics();

    private void updateStatistics(List<Event> outgoing) {

        for(Event e: outgoing) {

            if (e == null) {
                continue;
            }

            if (e instanceof FaultEvent) {

                faultStats.update((FaultEvent) e);
            }
            else if (e instanceof BusinessScenarioEvent) {

                bsStats.update((BusinessScenarioEvent)e);
            }
            else {

                otherEventsCount++;
            }
        }
    }

    private void displayStatistics() {

        Set<BusinessScenarioState> bsStates = bsStats.getBusinessScenarioStates();
        System.out.printf("business scenarios: %d (%s)\n",
                bsStats.getBusinessScenarioCount(), buildStateCountComment(bsStates));

        //
        // always start by reporting "COMPLETE" scenarios, those are the most important
        //
        displayScenarioStatsPerState(BusinessScenarioState.COMPLETE);

        //
        // then loop through the others
        //

        for(BusinessScenarioState s: bsStates) {
            if (s.equals(BusinessScenarioState.COMPLETE)) {
                // already reported
                continue;
            }
            displayScenarioStatsPerState(s);
        }

        System.out.println();
        System.out.printf("            faults: %d (%d different types)\n",
                faultStats.getFaultCount(), faultStats.getFaultTypeCount());

        Set<FaultType> faultTypes = faultStats.getFaultTypes();

        for(FaultType ft: faultTypes) {
            System.out.printf("                      %s: %d\n", ft, faultStats.getCountPerType(ft));
        }

        System.out.println();
        System.out.printf("      other events: %d\n", otherEventsCount);
        System.out.printf("     HTTP requests: %d\n", httpEventCount);
        System.out.printf("     HTTP sessions: %d\n", sessions.size());
    }

    private void displayScenarioStatsPerState(BusinessScenarioState state) {

        BusinessScenarioStateStatistics s = bsStats.getBusinessScenarioStatisticsPerState(state);

        //
        // special handling for COMPLETE scenarios
        //

        if (BusinessScenarioState.COMPLETE.equals(state)) {

            if (s == null) {
                System.out.printf("                      NO COMPLETE scenarios\n");
                return;
            }
        }

        long counterPerState = s.getBusinessScenarioCount();

        System.out.printf(
                "                      %s: %d, duration min/avg/max: %d/%d/%d ms, reqs/scenario min/avg/max: %d/%2.2f/%d\n",
                state.name(), counterPerState,
                s.getMinDurationMs(), s.getAverageDurationMs(), s.getMaxDurationMs(),
                s.getMinRequestsPerScenario(), s.getAverageRequestsPerScenario(), s.getMaxRequestsPerScenario());

        //
        // special handling for COMPLETE scenarios
        //

        if (BusinessScenarioState.COMPLETE.equals(state)) {

            System.out.printf(
                    "                        Successful COMPLETE scenarios (all requests are 200s): %d\n",
                    s.getSuccessfulCount());
        }
    }

    private String buildStateCountComment(Set<BusinessScenarioState> bsStates) {

        if (bsStates.isEmpty()) {
            return "NO business scenario states";
        }

        int size = bsStates.size();

        if (size == 1) {
            return "1 state only";
        }

        return size + " different states";
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}
