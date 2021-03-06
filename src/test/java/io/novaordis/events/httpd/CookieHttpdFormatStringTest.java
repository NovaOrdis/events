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

package io.novaordis.events.httpd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/4/16
 */
public class CookieHttpdFormatStringTest extends ParameterizedFormatStringTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void getLiteral() throws Exception {

        CookieHttpdFormatString i = new CookieHttpdFormatString("%{c,something}");

        assertEquals("%{c,something}", i.getLiteral());
    }

    @Test
    public void getLiteral_AlternativeFormat() throws Exception {

        CookieHttpdFormatString i = new CookieHttpdFormatString("%{something}c");

        assertEquals("%{something}c", i.getLiteral());
    }


    @Test
    public void literalStartsWithCookieSpecificationButAlsoContainsSomethingElse() throws Exception {

        CookieHttpdFormatString i = new CookieHttpdFormatString("%{c,something}blah");

        assertEquals("%{c,something}", i.getLiteral());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CookieHttpdFormatString getFormatStringToTest(String s) {

        if (s == null) {
            s = "%{c,something}";
        }

        return new CookieHttpdFormatString(s);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
