/*
 * (c) Copyright 2017 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.conjure.java.api.config.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.palantir.conjure.java.api.config.ssl.SslConfiguration;
import com.palantir.tokens.auth.BearerToken;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

/** A variant of {@link PartialServiceConfiguration} in which some fields (e.g., {@link #security}) are required. */
@JsonDeserialize(as = ImmutableServiceConfiguration.class)
@Value.Immutable
@ImmutablesStyle
public interface ServiceConfiguration {

    Optional<BearerToken> apiToken();

    SslConfiguration security();

    List<String> uris();

    Optional<Duration> connectTimeout();

    Optional<Duration> readTimeout();

    Optional<Duration> writeTimeout();

    Optional<Integer> maxNumRetries();

    /**
     * Indicates how the target node is selected for a given request.
     */
    Optional<NodeSelectionStrategy> nodeSelectionStrategy();

    /**
     * The amount of time a URL marked as failed should be avoided for subsequent calls. If the
     * {@link #nodeSelectionStrategy} is ROUND_ROBIN, this must be a positive period of time.
     */
    Optional<Duration> failedUrlCooldown();

    /**
     * The size of one backoff time slot for call retries. For example, an exponential backoff retry algorithm may
     * choose a backoff time in {@code [0, backoffSlotSize * 2^c]} for the c-th retry.
     */
    Optional<Duration> backoffSlotSize();

    /** Indicates whether client-side sympathetic QoS should be enabled. */
    Optional<ClientQoS> clientQoS();

    /** Indicates whether QosExceptions (other than RetryOther) should be propagated. */
    Optional<ServerQoS> serverQoS();

    Optional<Boolean> enableGcmCipherSuites();

    Optional<Boolean> fallbackToCommonNameVerification();

    Optional<ProxyConfiguration> proxy();

    static ImmutableServiceConfiguration.Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutableServiceConfiguration.Builder {}

    enum ClientQoS {
        /** Default. */
        ENABLED,

        /**
         * Disables the client-side sympathetic QoS. Consumers should almost never use this option, reserving it
         * for where there are known issues with the QoS interaction. Please consult project maintainers if applying
         * this option.
         */
        DANGEROUS_DISABLE_SYMPATHETIC_CLIENT_QOS
    }

    enum ServerQoS {
        /** Default. */
        AUTOMATIC_RETRY,

        /**
         * Propagate QosException.Throttle and QosException.Unavailable (429/503) to the caller. Consumers
         * should use this when an upstream service has better context on how to handle the QoS error. This delegates
         * the responsibility to the upstream service, which should use an appropriate conjure client to handle the
         * response.
         *
         * For example, let us imagine a proxy server that serves both interactive and long-running background requests
         * by dispatching requests to some backend. Interactive requests should be retried relatively few times in
         * comparison to background jobs which run for minutes our even hours. The proxy server should use a backend
         * client that propagates the QoS responses instead of retrying so the proxy client can handle them
         * appropriately. There is no risk of retry storms because the retries are isolated to one layer, the proxy
         * client.
         *
         * Note that QosException.RetryOther (308) is not propagated. If the proxy server is exposed on the front door
         * but the backend is not, it makes no sense to redirect the caller to a new backend. The client will still
         * follow redirects.
         */
        PROPAGATE_429_and_503_TO_CALLER
    }
}
