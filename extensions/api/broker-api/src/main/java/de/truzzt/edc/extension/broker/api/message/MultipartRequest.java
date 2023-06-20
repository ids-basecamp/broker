package de.truzzt.edc.extension.broker.api.message;


import de.truzzt.edc.extension.broker.api.types.ids.Message;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MultipartRequest {

    private final Message header;
    private final String payload;
    private final ClaimToken claimToken;

    private MultipartRequest(@NotNull Message header, @Nullable String payload, ClaimToken claimToken) {
        this.header = header;
        this.payload = payload;
        this.claimToken = claimToken;
    }

    @NotNull
    public Message getHeader() {
        return header;
    }

    @Nullable
    public String getPayload() {
        return payload;
    }

    @Nullable
    public ClaimToken getClaimToken() {
        return claimToken;
    }

    public static class Builder {

        private Message header;
        private String payload;
        private ClaimToken claimToken;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder header(@NotNull Message header) {
            this.header = header;
            return this;
        }

        public Builder payload(@Nullable String payload) {
            this.payload = payload;
            return this;
        }

        public Builder claimToken(@NotNull ClaimToken claimToken) {
            this.claimToken = claimToken;
            return this;
        }

        public MultipartRequest build() {
            Objects.requireNonNull(header, "Multipart request header is null.");
            Objects.requireNonNull(claimToken, "Multipart request claim token is null.");
            return new MultipartRequest(header, payload, claimToken);
        }
    }
}