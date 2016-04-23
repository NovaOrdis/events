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

import io.novaordis.events.core.event.Event;
import io.novaordis.events.core.event.FaultEvent;
import io.novaordis.events.core.event.IntegerProperty;
import io.novaordis.events.core.event.LongProperty;
import io.novaordis.events.httpd.HttpEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/4/16
 */
public class HttpSessionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HttpSessionTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        HttpSession s = new HttpSession("test");
        assertEquals("test", s.getJSessionId());
    }

    // processBusinessScenario() ---------------------------------------------------------------------------------------

    @Test
    public void processBusinessScenario_EventDoesNotBelongToTheSession() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        HttpEvent e = new HttpEvent(0L);

        try {
            s.processBusinessScenario(e);
            fail("should have thrown exception, the request is not associated with the session");
        }
        catch(IllegalArgumentException ex) {
            log.info(ex.getMessage());
        }
    }

    @Test
    public void processBusinessScenario_TwoRequestScenario() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        HttpEvent startRequest = new HttpEvent(7L);
        startRequest.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        startRequest.setLongProperty(HttpEvent.REQUEST_DURATION, 10L);
        startRequest.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-1");

        Event event = s.processBusinessScenario(startRequest);
        assertNull(event);

        HttpEvent stopRequest = new HttpEvent(8L);
        stopRequest.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        stopRequest.setLongProperty(HttpEvent.REQUEST_DURATION, 20L);
        stopRequest.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_STOP_MARKER_HEADER_NAME, "scenario-1");

        BusinessScenarioEvent bse = (BusinessScenarioEvent)s.processBusinessScenario(stopRequest);

        assertEquals("scenario-1", bse.getStringProperty(BusinessScenarioEvent.TYPE).getValue());
        assertEquals(2, bse.getIntegerProperty(BusinessScenarioEvent.REQUEST_COUNT).getInteger().intValue());
        assertEquals(30L, bse.getLongProperty(BusinessScenarioEvent.DURATION).getLong().longValue());
        assertEquals(7L, bse.getTimestamp().longValue());
    }

    @Test
    public void processBusinessScenario_JSessionIDPresent_NoBusinessScenarioMarker() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        HttpEvent e = new HttpEvent(1L);
        e.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e.setLongProperty(HttpEvent.REQUEST_DURATION, 1L);

        FaultEvent fe = (FaultEvent)s.processBusinessScenario(e);

        String msg = fe.toString();
        log.info(msg);
        assertTrue(msg.contains("there is no active business scenario for"));
    }

    @Test
    public void processBusinessScenario() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        Event re;

        HttpEvent e = new HttpEvent(10L);
        e.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-1");
        e.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));

        re = s.processBusinessScenario(e);
        assertNull(re);

        HttpEvent e2 = new HttpEvent(20L);
        e2.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e2.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));

        re = s.processBusinessScenario(e2);
        assertNull(re);

        HttpEvent e3 = new HttpEvent(30L);
        e3.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e3.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));
        e3.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_STOP_MARKER_HEADER_NAME);

        re = s.processBusinessScenario(e3);
        assertNotNull(re);

        BusinessScenarioEvent bse = (BusinessScenarioEvent)re;

        assertEquals(3, bse.getIntegerProperty(BusinessScenarioEvent.REQUEST_COUNT).getInteger().intValue());
        assertEquals(3L, bse.getLongProperty(BusinessScenarioEvent.DURATION).getLong().longValue());
        assertEquals(10L, bse.getTimestamp().longValue());
        assertEquals("scenario-1", bse.getStringProperty(BusinessScenarioEvent.TYPE).getString());

        //
        // successive scenario
        //

        HttpEvent e4 = new HttpEvent(40L);
        e4.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e4.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-2");
        e4.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));

        re = s.processBusinessScenario(e4);
        assertNull(re);

        HttpEvent e5 = new HttpEvent(50L);
        e5.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e5.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));
        e5.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_STOP_MARKER_HEADER_NAME);

        re = s.processBusinessScenario(e3);
        assertNotNull(re);

        BusinessScenarioEvent bse2 = (BusinessScenarioEvent)re;

        assertEquals(2, bse2.getIntegerProperty(BusinessScenarioEvent.REQUEST_COUNT).getInteger().intValue());
        assertEquals(2, bse2.getLongProperty(BusinessScenarioEvent.DURATION).getLong().longValue());
        assertEquals(40L, bse2.getTimestamp().longValue());
        assertEquals("scenario-2", bse2.getStringProperty(BusinessScenarioEvent.TYPE).getString());
    }

    @Test
    public void processBusinessScenario_StopMarkerSameTypeLabel() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        Event re;

        HttpEvent e = new HttpEvent(1L);
        e.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-1");
        e.setLongProperty(HttpEvent.REQUEST_DURATION, 1L);

        re = s.processBusinessScenario(e);
        assertNull(re);

        HttpEvent e2 = new HttpEvent(2L);
        e2.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e2.setProperty(new LongProperty(HttpEvent.REQUEST_DURATION, 1L));
        e2.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_STOP_MARKER_HEADER_NAME, "scenario-1");

        re = s.processBusinessScenario(e2);
        assertNotNull(re);

        BusinessScenarioEvent bse = (BusinessScenarioEvent)re;

        assertEquals(2, bse.getIntegerProperty(BusinessScenarioEvent.REQUEST_COUNT).getInteger().intValue());
        assertEquals(2, bse.getLongProperty(BusinessScenarioEvent.DURATION).getLong().longValue());
        assertEquals("scenario-1", bse.getStringProperty(BusinessScenarioEvent.TYPE).getString());
    }

    @Test
    public void processBusinessScenario_StopMarkerDifferentTypeLabel() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        Event re;

        HttpEvent e = new HttpEvent(1L);
        e.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-1");
        e.setRequestDuration(1L);

        re = s.processBusinessScenario(e);
        assertNull(re);

        HttpEvent e2 = new HttpEvent(2L);
        e2.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e2.setRequestDuration(2L);
        e2.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_STOP_MARKER_HEADER_NAME, "scenario-2");

        try {
            s.processBusinessScenario(e2);
            fail("should throw exception");
        }
        catch(IllegalArgumentException ex) {
            log.info(ex.getMessage());
        }
    }

    @Test
    public void processBusinessScenario_InvalidRequest() throws Exception {

        HttpSession s = new HttpSession("test-session-1");

        Event re;

        HttpEvent e = new HttpEvent(1L);
        e.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");
        e.setRequestHeader(BusinessScenario.BUSINESS_SCENARIO_START_MARKER_HEADER_NAME, "scenario-1");
        e.setRequestDuration(1L);

        re = s.processBusinessScenario(e);
        assertNull(re);

        HttpEvent noDurationRequest = new HttpEvent(2L);
        noDurationRequest.setCookie(HttpEvent.JSESSIONID_COOKIE_KEY, "test-session-1");

        FaultEvent fe = (FaultEvent)s.processBusinessScenario(noDurationRequest);
        log.info("" + fe);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
