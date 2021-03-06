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

package io.novaordis.events.extensions.bscenarios;

import io.novaordis.events.httpd.HttpEvent;

/**
 * Information about a HTTP request/response pair.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 5/17/16
 */
public class HttpRequestResponsePair {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String requestSequenceId;
    private Integer statusCode;

    // null means duration was not present in the original information
    private Long duration;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Builds a HTTP request representation based on a HttpEvent instance
     */
    public HttpRequestResponsePair(HttpEvent event) {

        if (event == null) {
            return;
        }

        this.requestSequenceId = event.getRequestSequenceId();

        // prefer original request status code
        this.statusCode = event.getOriginalRequestStatusCode();

        if (statusCode == null) {
            this.statusCode = event.getStatusCode();
        }

        this.duration = event.getRequestDuration();
    }

    public HttpRequestResponsePair() {

        this(null);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the request sequence ID, if present in the original HttpEvent, or null otherwise
     */
    public String getRequestSequenceId() {

        return requestSequenceId;
    }

    public void setRequestSequenceId(String s) {
        this.requestSequenceId = s;
    }

    /**
     * @return null if no status code was present in the original HTTP event
     */
    public Integer getStatusCode() {

        return statusCode;
    }

    public void setStatusCode(Integer i) {
        this.statusCode = i;
    }

    /**
     * @return may return null, which means the duration information was not available in the original request/response
     * pair information.
     */
    public Long getDuration() {
        return duration;
    }

    @Override
    public String toString() {

        return "N/A N/A " + (statusCode == null ? "N/A" : statusCode);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    void setDuration(Long duration) {
        this.duration = duration;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
