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

package io.novaordis.esa;

import io.novaordis.clad.UserErrorException;
import io.novaordis.esa.processor.EventCSVWriter;
import io.novaordis.esa.processor.HttpdLogParser;
import io.novaordis.esa.processor.InputStreamToEventConvertor;
import io.novaordis.esa.processor.SingleThreadedEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/21/16
 */
public class EventStreamAnalyzer {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EventStreamAnalyzer.class);

    public static final int BUFFER_SIZE = 1024 * 1024;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // CommandLineDriven implementation --------------------------------------------------------------------------------

    public void run() throws UserErrorException {

        SingleThreadedEventProcessor one = new SingleThreadedEventProcessor("file to event convertor");

        //        BufferedReader input = null;
//
//        try {
//
//            input = new BufferedReader(new InputStreamReader(System.in), BUFFER_SIZE);

        one.setInput(System.in);
        one.setByteLogic(new InputStreamToEventConvertor());
        one.setOutput(new ArrayBlockingQueue<>(10000));

        SingleThreadedEventProcessor two = new SingleThreadedEventProcessor("httpd log parser");
        two.setInput(one.getOutputQueue());
        two.setEventLogic(new HttpdLogParser());
        two.setOutput(new ArrayBlockingQueue<>(10000));

        SingleThreadedEventProcessor three = new SingleThreadedEventProcessor("csv writer");
        three.setInput(two.getOutputQueue());
        three.setEventLogic(new EventCSVWriter());
        three.setOutput(System.out);

        one.start();
        two.start();
        three.start();

        three.waitForEndOfStream();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
