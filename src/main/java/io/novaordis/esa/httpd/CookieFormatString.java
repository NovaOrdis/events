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

package io.novaordis.esa.httpd;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/4/16
 */
public class CookieFormatString extends ParameterizedFormatStringBase implements ParameterizedFormatString {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String PREFIX = "%{c,";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String cookie;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param formatStringLiteral as declared in the format specification, example: %{c,Some-Cookie}
     *
     * @throws IllegalArgumentException if the literal does not match the expected pattern.
     */
    public CookieFormatString(String formatStringLiteral) throws IllegalArgumentException {
        super(formatStringLiteral);
    }

    // ParameterizedFormatString implementation ------------------------------------------------------------------------

    @Override
    public String getLiteral() {

        return PREFIX + cookie + "}";
    }

    @Override
    public String getParameter() {

        return cookie;
    }

    @Override
    public void setParameter(String parameter) {

        cookie = parameter;
    }

    // ParameterizedFormatStringBase overrides -------------------------------------------------------------------------

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    @Override
    protected String getHttpEventMapName() {

        return HttpEvent.COOKIES;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getCookie() {
        return cookie;
    }

    @Override
    public String toString() {

        return getLiteral();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}