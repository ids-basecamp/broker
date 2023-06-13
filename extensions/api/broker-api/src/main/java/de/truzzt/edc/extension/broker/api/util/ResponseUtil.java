package de.truzzt.edc.extension.broker.api.util;

import de.truzzt.edc.extension.broker.api.message.MultipartResponse;
import de.truzzt.edc.extension.broker.api.util.dto.Message;
import de.truzzt.edc.extension.broker.api.util.dto.RejectionMessage;
import de.truzzt.edc.extension.broker.api.util.dto.RejectionReason;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.protocol.ids.spi.types.IdsType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.eclipse.edc.protocol.ids.util.CalendarUtil.gregorianNow;

public class ResponseUtil {

    public static MultipartResponse createMultipartResponse(@NotNull Message header) {
        return MultipartResponse.Builder.newInstance()
                .header(header)
                .build();
    }

    public static MultipartResponse createMultipartResponse(@NotNull Message header, @NotNull Object payload) {
        return MultipartResponse.Builder.newInstance()
                .header(header)
                .payload(payload)
                .build();
    }

    public static Message messageProcessedNotification(@NotNull Message correlationMessage,
                                                                                   @NotNull IdsId connectorId) {
        var messageId = getMessageId();

        Message message =  new Message(messageId);
        message.setContentVersion(IdsConstants.INFORMATION_MODEL_VERSION);
        message.setModelVersion(IdsConstants.INFORMATION_MODEL_VERSION);
        message.setIssued(gregorianNow());
        message.setIssuerConnector(connectorId.toUri());
        message.setSenderAgent(connectorId.toUri());
        message.setCorrelationMessage(correlationMessage.getId());
        message.setRecipientConnector(new ArrayList<>(Collections.singletonList(correlationMessage.getIssuerConnector())));
        message.setRecipientAgent(new ArrayList<>(Collections.singletonList(correlationMessage.getSenderAgent())));

        return message;
    }

    @NotNull
    public static RejectionMessage notAuthenticated(@NotNull Message correlationMessage,
                                                    @NotNull IdsId connectorId) {
        RejectionMessage rejectionMessage = createRejectionMessage(correlationMessage, connectorId);
        rejectionMessage.setRejectionReason(RejectionReason.NOT_AUTHENTICATED);

        return rejectionMessage;
    }

    @NotNull
    public static RejectionMessage malformedMessage(@Nullable Message correlationMessage,
                                                                                                @NotNull IdsId connectorId) {
        RejectionMessage rejectionMessage = createRejectionMessage(correlationMessage, connectorId);
        rejectionMessage.setRejectionReason(RejectionReason.MALFORMED_MESSAGE);

        return rejectionMessage;
    }

    @NotNull
    public static RejectionMessage messageTypeNotSupported(@NotNull Message correlationMessage,
                                                           @NotNull IdsId connectorId) {
        RejectionMessage rejectionMessage = createRejectionMessage(correlationMessage, connectorId);
        rejectionMessage.setRejectionReason(RejectionReason.MESSAGE_TYPE_NOT_SUPPORTED);

        return rejectionMessage;
    }

    @NotNull
    public static RejectionMessage badParameters(@NotNull Message correlationMessage,
                                                 @NotNull IdsId connectorId) {
        RejectionMessage rejectionMessage =  createRejectionMessage(correlationMessage, connectorId);
        rejectionMessage.setRejectionReason(RejectionReason.BAD_PARAMETERS);

        return rejectionMessage;
    }

    @NotNull
    public static RejectionMessage internalRecipientError(@NotNull Message correlationMessage,
                                                          @NotNull IdsId connectorId) {
        RejectionMessage rejectionMessage =  createRejectionMessage(correlationMessage, connectorId);
        rejectionMessage.setRejectionReason(RejectionReason.INTERNAL_RECIPIENT_ERROR);

        return rejectionMessage;
    }

    @NotNull
    private static RejectionMessage createRejectionMessage(@Nullable Message correlationMessage,
                                                           @NotNull IdsId connectorId) {
        var messageId = getMessageId();

        var rejectionMessage = new RejectionMessage(messageId);
        rejectionMessage.setContentVersion(IdsConstants.INFORMATION_MODEL_VERSION);
        rejectionMessage.setModelVersion(IdsConstants.INFORMATION_MODEL_VERSION);
        rejectionMessage.setIssued(gregorianNow());
        rejectionMessage.setIssuerConnector(connectorId.toUri());
        rejectionMessage.setSenderAgent(connectorId.toUri());

        if (correlationMessage != null) {
            rejectionMessage.setCorrelationMessage(correlationMessage.getId());
            rejectionMessage.setRecipientAgent(new ArrayList<>(Collections.singletonList(correlationMessage.getSenderAgent())));
            rejectionMessage.setRecipientConnector(new ArrayList<>(Collections.singletonList(correlationMessage.getIssuerConnector())));
        }

        return rejectionMessage;
    }

    private static URI getMessageId() {
        return IdsId.Builder.newInstance().value(UUID.randomUUID().toString()).type(IdsType.MESSAGE).build().toUri();
    }
}
