package de.truzzt.edc.extension.broker.api.types.ids;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.util.*;

public class Message {


    @JsonProperty("@context")
    @NotNull
    private String context;

    @JsonProperty("@id")
    @NotNull
    private URI id;

    @JsonProperty("@type")
    @NotNull
    private String type;

    @NotNull
    @JsonProperty("ids:securityToken")
    @JsonAlias({"ids:securityToken", "securityToken"})
    private DynamicAttributeToken securityToken;

    @NotNull
    @JsonProperty("ids:issuerConnector")
    @JsonAlias({"ids:issuerConnector", "issuerConnector"})
    private URI issuerConnector;

    @NotNull
    @JsonProperty("ids:modelVersion")
    @JsonAlias({"ids:modelVersion", "modelVersion"})
    String modelVersion;

    @JsonProperty("ids:correlationMessage")
    @JsonAlias({"ids:correlationMessage", "correlationMessage"})
    URI correlationMessage;

    @JsonProperty("ids:recipientConnector")
    @JsonAlias({"ids:recipientConnector", "recipientConnector"})
    List<URI> recipientConnector;

    @JsonProperty("ids:recipientAgent")
    @JsonAlias({"ids:recipientAgent", "recipientAgent"})
    List<URI> recipientAgent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSzzz")
    @NotNull
    @JsonProperty("ids:issued")
    @JsonAlias({"ids:issued", "issued"})
    XMLGregorianCalendar issued;


    @NotNull
    @JsonProperty("ids:senderAgent")
    @JsonAlias({"ids:senderAgent", "senderAgent"})
    private URI senderAgent;

    @JsonProperty("ids:contentVersion")
    @JsonAlias({"ids:contentVersion", "contentVersion"})
    String contentVersion;

    // all classes have a generic property array
    @JsonIgnore
    protected Map<String, Object> properties;

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
    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        if (this.properties == null)
            return null;
        Iterator<String> iter = this.properties.keySet().iterator();
        Map<String, Object> resultset = new HashMap<String, Object>();
        while (iter.hasNext()) {
            String key = iter.next();
            resultset.put(key, urifyObjects(this.properties.get(key)));
        }
        return resultset;
    }

    public Object urifyObjects(Object value) {
        if (value instanceof String && value.toString().startsWith("http")) {
            try {
                value = new URI(value.toString());
            } catch (Exception e) {
                /* do nothing */ }
        } else if (value instanceof ArrayList) {
            ArrayList<Object> result_array = new ArrayList<Object>();
            ((ArrayList) value).forEach(x -> result_array.add(urifyObjects(x)));
            return result_array;
        } else if (value instanceof Map) {
            Map<String, Object> result_map = new HashMap<String, Object>();
            ((Map) value).forEach((k, v) -> result_map.put(k.toString(), urifyObjects(v)));
            return result_map;
        }
        return value;
    }

    @JsonAnySetter
    public void setProperty(String property, Object value) {
        if (this.properties == null)
            this.properties = new HashMap<String, Object>();
        if (property.startsWith("@")) {
            return;
        } ;
        this.properties.put(property, value);
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getIssuerConnector() {
        return issuerConnector;
    }

    public void setIssuerConnector(URI issuerConnector) {
        this.issuerConnector = issuerConnector;
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

    public DynamicAttributeToken getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(DynamicAttributeToken securityToken) {
        this.securityToken = securityToken;
    }

    public URI getSenderAgent() {
        return senderAgent;
    }

    public void setSenderAgent(URI senderAgent) {
        this.senderAgent = senderAgent;
    }

    public String getContentVersion() {
        return contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}

