package de.truzzt.edc.extension.broker.api.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.fraunhofer.iais.eis.RejectionReasonBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:RejectionReason")
public class RejectionReason {

    @JsonProperty("@id")
    @JsonAlias({"@id", "id"})
    @NotNull
    protected URI id;

    public RejectionReason() {

    }
    public RejectionReason(URI id) {
        this.id = id;
    }


    public static final RejectionReason BAD_PARAMETERS = new RejectionReason(URI.create("https://w3id.org/idsa/code/BAD_PARAMETERS"));


    public static final RejectionReason  INTERNAL_RECIPIENT_ERROR =
            new RejectionReason(URI.create("https://w3id.org/idsa/code/INTERNAL_RECIPIENT_ERROR"));

    public static final RejectionReason MALFORMED_MESSAGE = new RejectionReason(URI.create("https://w3id.org/idsa/code/MALFORMED_MESSAGE"));

    public static final RejectionReason MESSAGE_TYPE_NOT_SUPPORTED =
            new RejectionReason(URI.create("https://w3id.org/idsa/code/MESSAGE_TYPE_NOT_SUPPORTED"));

    public static final RejectionReason METHOD_NOT_SUPPORTED =
            new RejectionReason(URI.create("https://w3id.org/idsa/code/METHOD_NOT_SUPPORTED"));

    public static final RejectionReason NOT_AUTHENTICATED = new RejectionReason(URI.create("https://w3id.org/idsa/code/NOT_AUTHENTICATED"));

    public static final RejectionReason NOT_AUTHORIZED = new RejectionReason(URI.create("https://w3id.org/idsa/code/NOT_AUTHORIZED"));

    public static final RejectionReason NOT_FOUND = new RejectionReason(URI.create("https://w3id.org/idsa/code/NOT_FOUND"));

    public static final RejectionReason TEMPORARILY_NOT_AVAILABLE =
            new RejectionReason(URI.create("https://w3id.org/idsa/code/TEMPORARILY_NOT_AVAILABLE"));

    public static final RejectionReason TOO_MANY_RESULTS = new RejectionReason(URI.create("https://w3id.org/idsa/code/TOO_MANY_RESULTS"));

    public static final RejectionReason VERSION_NOT_SUPPORTED =
            new RejectionReason(URI.create("https://w3id.org/idsa/code/VERSION_NOT_SUPPORTED"));

}
