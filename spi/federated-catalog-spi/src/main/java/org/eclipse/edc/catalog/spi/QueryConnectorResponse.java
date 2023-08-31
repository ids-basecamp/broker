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
 *       Truzzt GmbH - Initial implementation
 *
 */

package org.eclipse.edc.catalog.spi;

import java.util.ArrayList;
import java.util.List;

public class QueryConnectorResponse {
    private QueryConnectorResponse.Status status;
    private List<String> errors = new ArrayList<>();
    private List<FederatedCacheNode> nodes = new ArrayList<>();

    private QueryConnectorResponse(QueryConnectorResponse.Status status) {
        this.status = status;
        errors = new ArrayList<>();
    }

    public QueryConnectorResponse() {

    }

    public static QueryConnectorResponse ok(List<FederatedCacheNode> result) {
        return QueryConnectorResponse.Builder.newInstance()
                .status(QueryConnectorResponse.Status.ACCEPTED)
                .nodes(result)
                .build();
    }

    public List<FederatedCacheNode> getNodes() {
        return nodes;
    }

    public QueryConnectorResponse.Status getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public enum Status {
        ACCEPTED,
        NO_ADAPTER_FOUND
    }

    public static final class Builder {

        private final QueryConnectorResponse response;

        private Builder() {
            response = new QueryConnectorResponse();
            response.status = QueryConnectorResponse.Status.ACCEPTED; //thats the default
        }

        public static QueryConnectorResponse.Builder newInstance() {
            return new QueryConnectorResponse.Builder();
        }

        public QueryConnectorResponse.Builder nodes(List<FederatedCacheNode> assets) {
            response.nodes = assets;
            return this;
        }

        public QueryConnectorResponse.Builder status(QueryConnectorResponse.Status status) {
            response.status = status;
            return this;
        }

        public QueryConnectorResponse build() {
            return response;
        }

        public QueryConnectorResponse.Builder error(String error) {
            response.errors.add(error);
            return this;
        }
    }
}
