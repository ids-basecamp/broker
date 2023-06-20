package de.truzzt.edc.extension.broker.api.types.ids;

import com.fasterxml.jackson.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:tokenFormat")
public class TokenFormat {

    @JsonProperty("@type")
    @NotNull
    private String type;

    @JsonProperty("@id")
    @JsonAlias({"@id", "id"})
    @NotNull
    private URI id;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public String getType() {
        return "ids:tokenFormat";
    }
}
