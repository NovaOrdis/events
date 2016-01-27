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

package io.novaordis.esa.clad;

import io.novaordis.clad.ApplicationRuntime;
import io.novaordis.clad.Configuration;
import io.novaordis.clad.UserErrorException;
import io.novaordis.esa.core.EventProcessor;
import io.novaordis.esa.core.InputStreamInitiator;
import io.novaordis.esa.core.OutputStreamTerminator;
import io.novaordis.esa.core.event.Event;
import io.novaordis.esa.core.event.StringEventConverter;
import io.novaordis.esa.experimental.SampleToCSV;
import io.novaordis.esa.logs.httpd.HttpdLogParsingLogic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/16
 */
public class EventsApplicationRuntime implements ApplicationRuntime {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final int QUEUE_SIZE = 1000000;

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private InputStreamInitiator initiator;
    private EventProcessor httpdLogParser;
    private OutputStreamTerminator terminator;
    private CountDownLatch endOfStream;

    // Constructors ----------------------------------------------------------------------------------------------------

    // ApplicationRuntime implementation -------------------------------------------------------------------------------

    @Override
    public void init(Configuration configuration) throws UserErrorException {

        initiator = new InputStreamInitiator(
                "Input Stream Reader",
                System.in,
                new StringEventConverter(),
                new ArrayBlockingQueue<>(QUEUE_SIZE));

        httpdLogParser = new EventProcessor(
                "HTTP Log Parser",
                initiator.getOutputQueue(),
                new HttpdLogParsingLogic(),
                new ArrayBlockingQueue<>(QUEUE_SIZE));

        terminator = new OutputStreamTerminator(
                "CSV Writer",
                null,
                new SampleToCSV(),
                System.out);

        endOfStream = new CountDownLatch(1);
        terminator.addEndOfStreamListener(endOfStream::countDown);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public BlockingQueue<Event> getOutputQueue() {

        return httpdLogParser.getOutputQueue();
    }

    public void connectToTerminator(BlockingQueue<Event> queue) {
        terminator.setInputQueue(queue);
    }

    public void start() throws Exception {

        initiator.start();
        httpdLogParser.start();
        terminator.start();
    }

    public void waitForEndOfStream() throws InterruptedException {

        //
        // wait for the end of stream to propagate through the pipeline
        //
        endOfStream.await();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
