package de.truzzt.edc.extension.broker.api.message;

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class MultipartResponse {

    private final Message header;
    private final Object payload;

    private MultipartResponse(@NotNull Message header, @Nullable Object payload) {
        this.header = header;
        this.payload = payload;
    }

    @NotNull
    public Message getHeader() {
        return header;
    }

    @Nullable
    public Object getPayload() {
        return payload;
    }

    public void setSecurityToken(Function<Message, DynamicAttributeToken> getToken) {
        getHeader().setSecurityToken(getToken.apply(getHeader()));
    }

    public static class Builder {

        private Message header;
        private Object payload;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder header(@Nullable Message header) {
            this.header = header;
            return this;
        }

        public Builder payload(@Nullable Object payload) {
            this.payload = payload;
            return this;
        }

        public MultipartResponse build() {
            Objects.requireNonNull(header, "Multipart response header is null.");
            return new MultipartResponse(header, payload);
        }
    }
}
