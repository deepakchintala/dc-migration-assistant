/*
 * Copyright 2020 Atlassian
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

package com.atlassian.migration.datacenter.spi.infrastructure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect()
public class InfrastructureDeploymentStatus {

    private InfrastructureDeploymentState state;
    private String reason;

    public InfrastructureDeploymentStatus(InfrastructureDeploymentState state, String reason) {
        this.state = state;
        this.reason = reason;
    }

    @JsonProperty("state")
    public InfrastructureDeploymentState getState() {
        return state;
    }

    @JsonProperty("reason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getReason() {
        return reason;
    }
}
