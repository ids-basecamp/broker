package de.truzzt.edc.extension.broker.api.util.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.validation.constraints.NotNull;


/**
 * The payload of an JSON Web Token as a RDF class. Is used as the common parent of e.g., DatPayload
 * and DatRequestPayload.
 */

public class JWTPayload {

    @NotNull
    @JsonAlias({"https://w3id.org/idsa/core/sub", "ids:sub", "sub"})
    private String sub;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

}

