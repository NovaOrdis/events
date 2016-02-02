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

package io.novaordis.esa.event.special;

import io.novaordis.esa.event.OldEvent;
import io.novaordis.esa.event.OldProperty;

import java.util.Date;
import java.util.List;

/**
 * An event wrapper for lines. The wrapped String will never contain CR or LF.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/23/16
 */
public class StringOldEvent implements OldEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String s;

    // Constructors ----------------------------------------------------------------------------------------------------

    public StringOldEvent(String s) {
        this.s = s;
    }

    // Event implementation --------------------------------------------------------------------------------------------

    @Override
    public Date getTimestamp() {
        throw new RuntimeException("getTimestamp() NOT YET IMPLEMENTED");
    }

    @Override
    public List<OldProperty> getProperties() {
        throw new RuntimeException("getProperties() NOT YET IMPLEMENTED");
    }

    @Override
    public OldProperty getProperty(String name) {
        throw new RuntimeException("getProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public OldProperty getProperty(int index) {
        throw new RuntimeException("getProperty() NOT YET IMPLEMENTED");
    }

    @Override
    public OldProperty setProperty(int index, OldProperty property) {
        throw new RuntimeException("setProperty() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the wrapped string. May return an empty string but never null.
     */
    public String get() {

        return s;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
