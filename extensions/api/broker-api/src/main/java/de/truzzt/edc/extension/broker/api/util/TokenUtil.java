package de.truzzt.edc.extension.broker.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.truzzt.edc.extension.broker.api.util.dto.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class TokenUtil {

    public static Message parseMessage(InputStream streamToken, ObjectMapper mapper)  {
        try{
            JsonNode jsonMap = mapper.readTree(streamToken);
            return mapper.readValue(jsonMap.toString(), Message.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JWTPayload parseToken(Message message, ObjectMapper mapper){

        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = message.getSecurityToken().getTokenValue().split("\\.");
            return mapper.readValue(decoder.decode(chunks[1]), JWTPayload.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
