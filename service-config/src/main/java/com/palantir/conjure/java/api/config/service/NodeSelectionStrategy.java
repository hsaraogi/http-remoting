/*
 * (c) Copyright 2019 Palantir Technologies Inc. All rights reserved.
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

public enum NodeSelectionStrategy {

    /**
     * Once a node is found that returns responses successfully, use that node until a failure is received, then select
     * a new node. This strategy allows for the implementation to repin to a new node at any point in time. If this
     * behavior is not desirable, you can use {@link #PIN_UNTIL_ERROR_WITHOUT_RESHUFFLE}.
     */
    PIN_UNTIL_ERROR,

    /**
     * For each new request, select the "next" node (in some undefined order).
     */
    ROUND_ROBIN,

    /**
     * Similar to {@link #PIN_UNTIL_ERROR}, except will not shuffle the URLs throughout the lifetime of the client.
     */
    PIN_UNTIL_ERROR_WITHOUT_RESHUFFLE
}
