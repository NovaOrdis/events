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

package io.novaordis.esa.core;

import io.novaordis.esa.core.event.Event;

import java.util.List;

/**
 * The pluggable byte generation logic that turns events arriving over the input queue into bytes written on the
 * output stream.
 *
 * IMPORTANT: the conversion logic will be always invoked in a single threaded context.
 *
 * The main concern of the conversion logic is to single-threaded loop and convert events into bytes. The threading
 * and interaction with queues is the responsibility of the owner Terminator.
 *
 * @see Initiator
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/24/16
 */
public interface OutputStreamConversionLogic extends ConversionLogic {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if bytes are available for retrieval, and false if no byte is available for retrieval. If the
     * returned value is true, the byte(s) can be retrieved and at the same time removed with getBytes(), which is
     * guaranteed to return a non-empty array. The return value has advisory value only, no harm will come from
     * invoking getBytes() if process() returned false, however the result of the getEvents() invocation will be an
     * empty list. Presumably one could invoke process() multiple time without consulting the return value and only
     * belatedly invoke getBytes() - that would probably work assuming the conversion logic instance has sufficient
     * memory at its disposal to temporarily store the bytes.
     *
     * @see OutputStreamConversionLogic#getBytes()
     */
    boolean process(Event inputEvent);

    /**
     * Retrieves and at the same time removes from the instance any available bytes.
     *
     * If no bytes are available, the method will return an empty array, but never null.
     */
    byte[] getBytes();

}