/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *
 */

package org.eclipse.edc.catalog.spi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Object that contains information of a FederatedCacheNode. This is used by the {@link FederatedCacheNodeDirectory}.
 */
public class FederatedCacheNode {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("url")
    private final String targetUrl;
    @JsonProperty("supportedProtocols")
    private final List<String> supportedProtocols;
    @JsonProperty("onlineStatus")
    private final Boolean onlineStatus;
    @JsonProperty("lastCrawled")
    private final ZonedDateTime lastCrawled;
    @JsonProperty("contractOffersCount")
    private final Integer contractOffersCount;

    @JsonCreator
    public FederatedCacheNode(@JsonProperty("name") String name,
                              @JsonProperty("url") String targetUrl,
                              @JsonProperty("supportedProtocols") List<String> supportedProtocols) {
        this.name = name;
        this.targetUrl = targetUrl;
        this.supportedProtocols = supportedProtocols;

        this.onlineStatus = null;
        this.lastCrawled = null;
        this.contractOffersCount = null;
    }

    public FederatedCacheNode(@JsonProperty("name") String name,
                              @JsonProperty("onlineStatus") Boolean onlineStatus,
                              @JsonProperty("lastCrawled") ZonedDateTime lastCrawled,
                              @JsonProperty("contractOffersCount") Integer contractOffersCount) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.lastCrawled = lastCrawled;
        this.contractOffersCount = contractOffersCount;

        this.targetUrl = null;
        this.supportedProtocols = null;
    }

    public FederatedCacheNode(String name, String targetUrl, List<String> supportedProtocols, Boolean onlineStatus, ZonedDateTime lastCrawled, Integer contractOffersCount) {
        this.name = name;
        this.targetUrl = targetUrl;
        this.supportedProtocols = supportedProtocols;
        this.onlineStatus = onlineStatus;
        this.lastCrawled = lastCrawled;
        this.contractOffersCount = contractOffersCount;
    }

    public String getName() {
        return name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public ZonedDateTime getLastCrawled() {
        return lastCrawled;
    }

    public Integer getContractOffersCount() {
        return contractOffersCount;
    }

}
