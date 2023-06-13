package de.truzzt.edc.extension.broker.api.util.dto;

import com.fasterxml.jackson.annotation.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.VocabUtil;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("ids:TokenFormat")
public class TokenFormat {

    @JsonProperty("@id")
    @JsonAlias({"@id", "id"})
    @NotNull
    protected URI id;

   public URI getId() {
        return id;
    }

}
