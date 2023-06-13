package de.truzzt.edc.extension.broker.api.util.dto;

import com.fasterxml.jackson.annotation.*;
import de.fraunhofer.iais.eis.util.VocabUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:DynamicAttributeToken")
public class DynamicAttributeToken {

    @JsonProperty("@id")
    @JsonAlias({"@id", "id"})
    @NotNull
    protected URI id;

    @NotNull
    @JsonAlias({"ids:tokenFormat", "tokenFormat"})
    protected TokenFormat tokenFormat;

    @NotNull
    @JsonAlias({"ids:tokenValue", "tokenValue"})
    protected String tokenValue;

    protected DynamicAttributeToken() {
        id = VocabUtil.getInstance().createRandomUrl("dynamicAttributeToken");
    }

    @JsonProperty("@id")
    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public TokenFormat getTokenFormat() {
        return tokenFormat;
    }

    public void setTokenFormat(TokenFormat _tokenFormat) {
        this.tokenFormat = _tokenFormat;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String _tokenValue) {
        this.tokenValue = _tokenValue;
    }
}

