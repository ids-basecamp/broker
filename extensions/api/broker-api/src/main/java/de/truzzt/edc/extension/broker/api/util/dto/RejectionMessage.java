package de.truzzt.edc.extension.broker.api.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.net.URI;

public class RejectionMessage extends Message {

    @JsonAlias({"https://w3id.org/idsa/core/rejectionReason", "ids:rejectionReason", "rejectionReason"})
    RejectionReason rejectionReason;

    public RejectionMessage() {
    }

    public RejectionMessage(URI id) {
        super(id);
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
