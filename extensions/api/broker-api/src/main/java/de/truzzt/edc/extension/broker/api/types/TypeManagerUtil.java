package de.truzzt.edc.extension.broker.api.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.truzzt.edc.extension.broker.api.types.ids.DynamicAttributeToken;
import de.truzzt.edc.extension.broker.api.types.ids.Message;
import de.truzzt.edc.extension.broker.api.types.jwt.JWTPayload;
//import org.eclipse.edc.protocol.ids.jsonld.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class TypeManagerUtil {

    private final ObjectMapper mapper;

    public TypeManagerUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Message parseMessage(InputStream streamToken) {
        try {
            return mapper.readValue(streamToken, Message.class);
        } catch (IOException e) {
            throw new EdcException("Error parsing Header to Message", e);
        }
    }

    public JWTPayload parseToken(DynamicAttributeToken token) {
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = token.getTokenValue().split("\\.");
            return mapper.readValue(decoder.decode(chunks[1]), JWTPayload.class);

        } catch (IOException e) {
            throw new EdcException("Error parsing Token", e);
        }
    }

    public byte[] toJson(Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new EdcException("Error converting to JSON", e);
        }
    }
}
