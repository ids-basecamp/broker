package de.truzzt.edc.extension.broker.api.types.ids.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class VocabUtil {
    public static String randomUrlBase;

    public static URI createRandomUrl(String path) {
        try {
            if (randomUrlBase != null) {
                if (!randomUrlBase.endsWith("/")) {
                    randomUrlBase = randomUrlBase + "/";
                }

                return (new URL(randomUrlBase + path + "/" + UUID.randomUUID())).toURI();
            } else {
                return (new URL("https", "w3id.org", "/idsa/autogen/" + path + "/" + UUID.randomUUID())).toURI();
            }
        } catch (URISyntaxException | MalformedURLException var3) {
            throw new RuntimeException(var3);
        }
    }
}
