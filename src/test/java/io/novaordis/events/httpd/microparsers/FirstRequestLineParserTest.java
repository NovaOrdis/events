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

package io.novaordis.events.httpd.microparsers;

import io.novaordis.utilities.parsing.ParsingException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 6/30/16
 */
public class FirstRequestLineParserTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(FirstRequestLineParserTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void identifyEnd() throws Exception {

        String line = "GET / HTTP/1.1 something else";
        int startFrom = 0;

        int result = FirstRequestLineParser.identifyEnd(line, startFrom);
        assertEquals(14, result);
    }

    @Test
    public void identifyEnd_EndOfLine() throws Exception {

        String line = "GET / HTTP/1.1";
        int startFrom = 0;

        int result = FirstRequestLineParser.identifyEnd(line, startFrom);
        assertEquals(-1, result);
    }

    @Test
    public void identifyEnd_parsingFailure() throws Exception {

        String line = "blah";
        int startFrom = -1;

        try {

            FirstRequestLineParser.identifyEnd(line, startFrom);
            fail("should have thrown exception");
        }
        catch(ParsingException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("not found"));
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
