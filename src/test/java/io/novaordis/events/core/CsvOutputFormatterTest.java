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

package io.novaordis.events.core;

import io.novaordis.events.core.event.Event;
import io.novaordis.events.core.event.FaultEvent;
import io.novaordis.events.core.event.MockEvent;
import io.novaordis.events.core.event.MockProperty;
import io.novaordis.events.core.event.MockTimedEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/2/16
 */
public class CsvOutputFormatterTest extends OutputStreamConversionLogicTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CsvOutputFormatterTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // process() -------------------------------------------------------------------------------------------------------

    @Test
    public void process_WeDumpTheFaultOnFaultEvent() throws Exception {

        CsvOutputFormatter c = getConversionLogicToTest();

        Event event = new FaultEvent("test message", new RuntimeException("SYNTHETIC"));
        assertTrue(c.process(event));

        byte[] content = c.getBytes();
        String s = new String(content);

        //
        // TODO we may want to consider to send the fault events to stderr
        //

        assertEquals(event.toString() + "\n", s);
    }

    @Test
    public void process_RegularUntimedEvent_NoConfiguredOutputFormat() throws Exception {

        CsvOutputFormatter c = getConversionLogicToTest();

        //
        // make sure no output format is configured, the default formatter provided by the sub-class may come with
        // an output format on its own
        //
        c.setOutputFormat(null);

        MockEvent me = new MockEvent();

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        assertTrue(c.process(me));

        byte[] content = c.getBytes();
        String s = new String(content);

        assertEquals("C value, B value, A value\n", s);
    }

    @Test
    public void process_RegularTimedEvent_NoConfiguredOutputFormat() throws Exception {

        CsvOutputFormatter c = getConversionLogicToTest();

        //
        // make sure no output format is configured, the default formatter provided by the sub-class may come with
        // an output format on its own
        //
        c.setOutputFormat(null);

        Date d = new SimpleDateFormat("MM/yy/dd HH:mm:ss").parse("01/16/01 01:01:01");

        MockTimedEvent me = new MockTimedEvent(d.getTime());

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        assertTrue(c.process(me));

        byte[] content = c.getBytes();
        String s = new String(content);

        String expected = CsvOutputFormatter.DEFAULT_TIMESTAMP_FORMAT.format(d) + ", C value, B value, A value\n";
        assertEquals(expected, s);
    }

    @Test
    public void process_RegularUntimedEvent_WithConfiguredOutputFormat() throws Exception {

        CsvOutputFormatter c = getConversionLogicToTest();

        c.setOutputFormat("B, no-such-property, C");

        MockEvent me = new MockEvent();

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        assertTrue(c.process(me));

        byte[] content = c.getBytes();
        String s = new String(content);

        assertEquals("B value, , C value\n", s);
    }

    @Test
    public void process_RegularTimedEvent_WithConfiguredOutputFormat() throws Exception {

        CsvOutputFormatter c = getConversionLogicToTest();

        c.setOutputFormat("B, no-such-property, timestamp, C");

        Date d = new SimpleDateFormat("MM/yy/dd HH:mm:ss").parse("01/16/01 01:01:01");

        MockTimedEvent me = new MockTimedEvent(d.getTime());

        // priority inverse to the name order
        me.setProperty(new MockProperty("A", "A value", 3));
        me.setProperty(new MockProperty("B", "B value", 2));
        me.setProperty(new MockProperty("C", "C value", 1));

        assertTrue(c.process(me));

        byte[] content = c.getBytes();
        String s = new String(content);

        String expected = "B value, , " + CsvOutputFormatter.DEFAULT_TIMESTAMP_FORMAT.format(d) + ", C value\n";
        assertEquals(expected, s);
    }

    // setOutputFormat() -----------------------------------------------------------------------------------------------

    @Test
    public void setOutputFormat_Null() throws Exception {

        CsvOutputFormatter o = getConversionLogicToTest();
        o.setOutputFormat(null);
        assertNull(o.getOutputFormat());
    }

    @Test
    public void setOutputFormat_OneField() throws Exception {

        CsvOutputFormatter o = getConversionLogicToTest();

        o.setOutputFormat("a");

        String s = o.getOutputFormat();
        assertEquals("a", s);
    }

    @Test
    public void setOutputFormat_TwoFields() throws Exception {

        CsvOutputFormatter o = getConversionLogicToTest();

        o.setOutputFormat("a,b");

        String s = o.getOutputFormat();
        assertEquals("a, b", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CsvOutputFormatter getConversionLogicToTest() throws Exception {

        return new CsvOutputFormatter();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}