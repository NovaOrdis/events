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

package io.novaordis.esa.processor;

import io.novaordis.esa.event.Event;
import io.novaordis.esa.event.special.EndOfStreamEvent;
import io.novaordis.esa.event.special.StringEvent;
import io.novaordis.esa.logs.httpd.LogFormat;
import io.novaordis.esa.logs.httpd.LogLine;
import io.novaordis.esa.logs.httpd.LogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/16
 */
public class HttpdLogParser implements EventLogic {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HttpdLogParser.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private LogParser parser;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HttpdLogParser() {

        // TODO - how do I infer the log format from the log file? I need to externalize it in a friendly way
        this.parser = new LogParser(LogFormat.PERFORMANCE_ANALYSIS);

    }

    // EventLogic ------------------------------------------------------------------------------------------------------

    @Override
    public List<Event> process(Event inputEvent) {

        //
        // we only process StringEvents and EndOfStreamEvents, we warn for everything else
        //

        if (inputEvent instanceof EndOfStreamEvent) {

            return Collections.singletonList(inputEvent);
        }
        else if (inputEvent instanceof StringEvent) {

            String s = ((StringEvent)inputEvent).get();

            try {

                LogLine logLine = parser.parse(s);
                Event outputEvent = LogLine.toEvent(logLine);
                return Collections.singletonList(outputEvent);
            }
            catch (Exception e) {

                log.error("parsing failed", e);
                return Collections.emptyList();
            }
        }
        else {
            log.warn("unknown event type " + inputEvent + ", ignoring ...");
            return Collections.emptyList();
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
