package de.truzzt.edc.extension.broker.api.types.ids;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.util.List;

public class Message {

    @JsonProperty("@id")
    @NotNull
    private URI id;

    @JsonProperty("@type")
    @NotNull
    private String type;

    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/issuerConnector", "ids:issuerConnector", "issuerConnector"})
    private URI issuerConnector;

    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/modelVersion", "ids:modelVersion", "modelVersion"})
    String modelVersion;

    @JsonAlias({"https://w3id.org/idsa/core/correlationMessage", "ids:correlationMessage", "correlationMessage"})
    URI correlationMessage;

    @JsonAlias({"https://w3id.org/idsa/core/recipientConnector", "ids:recipientConnector", "recipientConnector"})
    List<URI> recipientConnector;

    @JsonAlias({"https://w3id.org/idsa/core/recipientAgent", "ids:recipientAgent", "recipientAgent"})
    List<URI> recipientAgent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSzzz")
    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/issued", "ids:issued", "issued"})
    XMLGregorianCalendar issued;

    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/securityToken", "ids:securityToken", "securityToken"})
    private DynamicAttributeToken securityToken;

    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/senderAgent", "ids:senderAgent", "senderAgent"})
    private URI senderAgent;

    @JsonAlias({"https://w3id.org/idsa/core/contentVersion", "ids:contentVersion", "contentVersion"})
    String contentVersion;

    public Message() {
    }
    public Message(URI id) {
        this.id = id;
    }
    public Message(URI id, URI issuerConnector, DynamicAttributeToken securityToken, URI senderAgent) {
        this.id = id;
        this.issuerConnector = issuerConnector;
        this.securityToken = securityToken;
        this.senderAgent = senderAgent;
    }

    public URI getIssuerConnector() {
        return issuerConnector;
    }

    public void setIssuerConnector(URI issuerConnector) {
        this.issuerConnector = issuerConnector;
    }

    public DynamicAttributeToken getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(DynamicAttributeToken securityToken) {
        this.securityToken = securityToken;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public URI getSenderAgent() {
        return senderAgent;
    }

    public void setSenderAgent(URI senderAgent) {
        this.senderAgent = senderAgent;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public URI getCorrelationMessage() {
        return correlationMessage;
    }

    public void setCorrelationMessage(URI correlationMessage) {
        this.correlationMessage = correlationMessage;
    }

    public List<URI> getRecipientConnector() {
        return recipientConnector;
    }

    public void setRecipientConnector(List<URI> recipientConnector) {
        this.recipientConnector = recipientConnector;
    }

    public List<URI> getRecipientAgent() {
        return recipientAgent;
    }

    public void setRecipientAgent(List<URI> recipientAgent) {
        this.recipientAgent = recipientAgent;
    }

    public XMLGregorianCalendar getIssued() {
        return issued;
    }

    public void setIssued(XMLGregorianCalendar issued) {
        this.issued = issued;
    }

    public String getContentVersion() {
        return contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

