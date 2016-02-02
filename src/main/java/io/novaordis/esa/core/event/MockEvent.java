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

package io.novaordis.esa.core.event;

import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/24/16
 */
public class MockEvent implements Event {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Object payload;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockEvent() {
        this(null);
    }

    public MockEvent(Object payload) {
        this.payload = payload;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setPayload(Object o) {
        this.payload = o;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public Set<Property> getProperties() {
        throw new RuntimeException("getProperties() NOT YET IMPLEMENTED");
    }

    @Override
    public Property getProperty(String name) {
        throw new RuntimeException("getProperty() NOT YET IMPLEMENTED");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
