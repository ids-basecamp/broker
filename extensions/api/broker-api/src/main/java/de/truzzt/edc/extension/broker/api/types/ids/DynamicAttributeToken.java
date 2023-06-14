package de.truzzt.edc.extension.broker.api.types.ids;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.truzzt.edc.extension.broker.api.types.ids.util.VocabUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:DynamicAttributeToken")
public class DynamicAttributeToken {

    @JsonProperty("@id")
    @JsonAlias({"@id", "id"})
    @NotNull
    private URI id;

    @NotNull
    @JsonAlias({"ids:tokenFormat", "tokenFormat"})
    private TokenFormat tokenFormat;

    @NotNull
    @JsonAlias({"ids:tokenValue", "tokenValue"})
    private String tokenValue;

    private DynamicAttributeToken() {
        id = VocabUtil.createRandomUrl("dynamicAttributeToken");
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

