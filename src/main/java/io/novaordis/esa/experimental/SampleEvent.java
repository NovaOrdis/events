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

package io.novaordis.esa.experimental;

import io.novaordis.esa.core.event.TimedEvent;
import io.novaordis.esa.core.event.GenericTimedEvent;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/25/16
 */
public class SampleEvent extends GenericTimedEvent implements TimedEvent {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long count;
    private double averageTime;

    // Constructors ----------------------------------------------------------------------------------------------------

    public SampleEvent(long timestamp) {

        super(timestamp);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setCount(long c) {
        this.count = c;
    }

    public long getCount() {
        return count;
    }

    public void setAverageTime(double d) {
        this.averageTime = d;
    }

    public double getAverageTime() {
        return averageTime;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
