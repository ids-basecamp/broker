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

package org.eclipse.edc.catalog.api.broker;

import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.EntityPart;

import java.util.List;

@Consumes({"multipart/form-data", "multipart/mixed"})
@Produces({ MediaType.APPLICATION_JSON })
@Path("/broker")
public class BrokerApiController {

    @POST
    @Path("/infrastructure")
    public MessageProcessedNotificationMessage infrastructure(
            List<EntityPart> parts
    ) {
        return new MessageProcessedNotificationMessageBuilder().build();
    }
}
