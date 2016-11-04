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

package io.novaordis.events.core.event;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/2/16
 */
public class PropertyFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(PropertyFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // createInstance() ------------------------------------------------------------------------------------------------

    @Test
    public void createInstance_String() throws Exception {

        StringProperty sp = (StringProperty)PropertyFactory.createInstance("test", String.class, "something", null);

        assertEquals("test", sp.getName());
        assertEquals(String.class, sp.getType());
        assertEquals("something", sp.getString());
    }

    @Test
    public void createInstance_String_Null() throws Exception {

        StringProperty sp = (StringProperty)PropertyFactory.createInstance("test", String.class, null, null);
        assertEquals("test", sp.getName());
        assertEquals(String.class, sp.getType());
        assertNull(sp.getString());
    }

    @Test
    public void createInstance_String_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", String.class, 1, null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void createInstance_Integer() throws Exception {

        IntegerProperty ip = (IntegerProperty)PropertyFactory.createInstance("test", Integer.class, 1, null);

        assertEquals("test", ip.getName());
        assertEquals(Integer.class, ip.getType());
        assertEquals(1, ip.getInteger().intValue());
    }

    @Test
    public void createInstance_Integer_NullMultiplicationFactor() throws Exception {

        IntegerProperty ip = (IntegerProperty)PropertyFactory.createInstance("test", Integer.class, 1, null, null);

        assertEquals("test", ip.getName());
        assertEquals(Integer.class, ip.getType());
        assertEquals(1, ip.getInteger().intValue());
    }

    @Test
    public void createInstance_Integer_MultiplicationFactor() throws Exception {

        IntegerProperty ip = (IntegerProperty)PropertyFactory.createInstance("test", Integer.class, 1, 10d, null);

        assertEquals("test", ip.getName());
        assertEquals(Integer.class, ip.getType());
        assertEquals(10, ip.getInteger().intValue());
    }

    @Test
    public void createInstance_Integer_Null() throws Exception {

        IntegerProperty ip = (IntegerProperty)PropertyFactory.createInstance("test", Integer.class, null, null);

        assertEquals("test", ip.getName());
        assertEquals(Integer.class, ip.getType());
        assertNull(ip.getInteger());
    }

    @Test
    public void createInstance_Integer_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", Integer.class, "1", null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void createInstance_Long() throws Exception {

        LongProperty ip = (LongProperty)PropertyFactory.createInstance("test", Long.class, 1L, null);

        assertEquals("test", ip.getName());
        assertEquals(Long.class, ip.getType());
        assertEquals(1L, ip.getLong().longValue());
    }

    @Test
    public void createInstance_Long_NullMultiplicationFactor() throws Exception {

        LongProperty ip = (LongProperty)PropertyFactory.createInstance("test", Long.class, 1L, null, null);

        assertEquals("test", ip.getName());
        assertEquals(Long.class, ip.getType());
        assertEquals(1L, ip.getLong().longValue());
    }

    @Test
    public void createInstance_Long_MultiplicationFactor() throws Exception {

        LongProperty ip = (LongProperty)PropertyFactory.createInstance("test", Long.class, 1L, 10d, null);

        assertEquals("test", ip.getName());
        assertEquals(Long.class, ip.getType());
        assertEquals(10L, ip.getLong().longValue());
    }

    @Test
    public void createInstance_Long_Null() throws Exception {

        LongProperty ip = (LongProperty)PropertyFactory.createInstance("test", Long.class, null, null);

        assertEquals("test", ip.getName());
        assertEquals(Long.class, ip.getType());
        assertNull(ip.getLong());
    }

    @Test
    public void createInstance_Long_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", Long.class, "1", null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void createInstance_Double() throws Exception {

        DoubleProperty dp = (DoubleProperty)PropertyFactory.createInstance("test", Double.class, 1d, null);

        assertEquals("test", dp.getName());
        assertEquals(Double.class, dp.getType());
        assertEquals(1.0, dp.getDouble().doubleValue(), 0.0001);
    }

    @Test
    public void createInstance_Double_NullMultiplicationFactor() throws Exception {

        DoubleProperty dp = (DoubleProperty)PropertyFactory.createInstance("test", Double.class, 1d, null, null);

        assertEquals("test", dp.getName());
        assertEquals(Double.class, dp.getType());
        assertEquals(1L, dp.getDouble().doubleValue(), 0.0001);
    }

    @Test
    public void createInstance_Double_MultiplicationFactor() throws Exception {

        DoubleProperty dp = (DoubleProperty)PropertyFactory.createInstance("test", Double.class, 1d, 10d, null);

        assertEquals("test", dp.getName());
        assertEquals(Double.class, dp.getType());
        assertEquals(10.0, dp.getDouble().doubleValue(), 0.0001);
    }

    @Test
    public void createInstance_Double_Null() throws Exception {

        DoubleProperty dp = (DoubleProperty)PropertyFactory.createInstance("test", Double.class, null, null);

        assertEquals("test", dp.getName());
        assertEquals(Double.class, dp.getType());
        assertNull(dp.getDouble());
    }

    @Test
    public void createInstance_Double_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", Double.class, "1", null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void createInstance_Float() throws Exception {

        FloatProperty fp = (FloatProperty)PropertyFactory.createInstance("test", Float.class, 1.1f, null);

        assertEquals("test", fp.getName());
        assertEquals(Float.class, fp.getType());
        assertEquals(1.1f, fp.getFloat().floatValue(), 0.0001);
    }

    @Test
    public void createInstance_Float_NullMultiplicationFactor() throws Exception {

        FloatProperty fp = (FloatProperty)PropertyFactory.createInstance("test", Float.class, 1.1f, null, null);

        assertEquals("test", fp.getName());
        assertEquals(Float.class, fp.getType());
        assertEquals(1.1f, fp.getFloat().floatValue(), 0.0001);
    }

    @Test
    public void createInstance_Float_MultiplicationFactor() throws Exception {

        FloatProperty fp = (FloatProperty)PropertyFactory.createInstance("test", Float.class, 1.1f, 10d, null);

        assertEquals("test", fp.getName());
        assertEquals(Float.class, fp.getType());
        assertEquals(11.0f, fp.getFloat().floatValue(), 0.0001);
    }

    @Test
    public void createInstance_Float_Null() throws Exception {

        FloatProperty fp = (FloatProperty)PropertyFactory.createInstance("test", Float.class, null, null);

        assertEquals("test", fp.getName());
        assertEquals(Float.class, fp.getType());
        assertNull(fp.getFloat());
    }

    @Test
    public void createInstance_Float_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", Float.class, "1", null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void createInstance_Map() throws Exception {

        Map<String, String> map = new HashMap<>();
        MapProperty mp = (MapProperty)PropertyFactory.createInstance("test", Map.class, map, null);

        assertEquals("test", mp.getName());
        assertEquals(Map.class, mp.getType());
        assertEquals(map, mp.getMap());
    }

    @Test
    public void createInstance_Map_Null() throws Exception {

        MapProperty mp = (MapProperty)PropertyFactory.createInstance("test", Map.class, null, null);

        assertEquals("test", mp.getName());
        assertEquals(Map.class, mp.getType());
        Map<String, Object> map = mp.getMap();
        assertNotNull(map);
        assertTrue(map.isEmpty());
    }

    @Test
    public void createInstance_Map_TypeMismatch() throws Exception {

        try {
            PropertyFactory.createInstance("test", Map.class, "1", null);
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // conversions -----------------------------------------------------------------------------------------------------

    @Test
    public void createInstance_StringToIntegerConversion() throws Exception {

        IntegerProperty ip = (IntegerProperty)PropertyFactory.createInstance("test", Integer.class, "1", null);

        assertEquals("test", ip.getName());
        assertEquals(Integer.class, ip.getType());
        assertEquals(1, ip.getInteger().intValue());
    }

    @Test
    public void createInstance_StringToIntegerConversionFails() throws Exception {

        try {
            PropertyFactory.createInstance("test", Integer.class, "blah", null);
            fail("should throw Exception");
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("cannot convert \"blah\" to an integer", msg);
        }
    }

    @Test
    public void createInstance_StringToLongConversion() throws Exception {

        LongProperty lp = (LongProperty)PropertyFactory.createInstance("test", Long.class, "1", null);

        assertEquals("test", lp.getName());
        assertEquals(Long.class, lp.getType());
        assertEquals(1L, lp.getLong().longValue());
    }

    @Test
    public void createInstance_StringToLongConversionFails() throws Exception {

        try {
            PropertyFactory.createInstance("test", Long.class, "blah", null);
            fail("should throw Exception");
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("cannot convert \"blah\" to a long", msg);
        }
    }

    @Test
    public void createInstance_StringToDoubleConversion() throws Exception {

        DoubleProperty dp = (DoubleProperty)PropertyFactory.createInstance("test", Double.class, "1.1", null);

        assertEquals("test", dp.getName());
        assertEquals(Double.class, dp.getType());
        assertEquals(1.1d, dp.getDouble().doubleValue(), 0.0001);
    }

    @Test
    public void createInstance_StringToDoubleConversionFails() throws Exception {

        try {
            PropertyFactory.createInstance("test", Double.class, "blah", null);
            fail("should throw Exception");
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertEquals("cannot convert \"blah\" to a double", msg);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}