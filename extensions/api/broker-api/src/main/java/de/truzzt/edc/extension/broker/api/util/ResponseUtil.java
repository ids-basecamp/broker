package de.truzzt.edc.extension.broker.api.util;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.truzzt.edc.extension.broker.api.message.MultipartResponse;
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

    public static MessageProcessedNotificationMessage messageProcessedNotification(@NotNull Message correlationMessage,
                                                                                   @NotNull IdsId connectorId) {
        var messageId = getMessageId();

        return new MessageProcessedNotificationMessageBuilder(messageId)
                ._contentVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(gregorianNow())
                ._issuerConnector_(connectorId.toUri())
                ._senderAgent_(connectorId.toUri())
                ._correlationMessage_(correlationMessage.getId())
                ._recipientConnector_(new ArrayList<>(Collections.singletonList(correlationMessage.getIssuerConnector())))
                ._recipientAgent_(new ArrayList<>(Collections.singletonList(correlationMessage.getSenderAgent())))
                .build();
    }

    @NotNull
    public static RejectionMessage notAuthenticated(@NotNull Message correlationMessage,
                                                    @NotNull IdsId connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.NOT_AUTHENTICATED)
                .build();
    }

    @NotNull
    public static RejectionMessage malformedMessage(@Nullable Message correlationMessage,
                                                    @NotNull IdsId connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.MALFORMED_MESSAGE)
                .build();
    }

    @NotNull
    public static RejectionMessage messageTypeNotSupported(@NotNull Message correlationMessage,
                                                           @NotNull IdsId connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.MESSAGE_TYPE_NOT_SUPPORTED)
                .build();
    }

    @NotNull
    public static RejectionMessage badParameters(@NotNull Message correlationMessage,
                                                 @NotNull IdsId connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.BAD_PARAMETERS)
                .build();
    }

    @NotNull
    public static RejectionMessage internalRecipientError(@NotNull Message correlationMessage,
                                                          @NotNull IdsId connectorId) {
        return createRejectionMessageBuilder(correlationMessage, connectorId)
                ._rejectionReason_(RejectionReason.INTERNAL_RECIPIENT_ERROR)
                .build();
    }

    @NotNull
    private static RejectionMessageBuilder createRejectionMessageBuilder(@Nullable Message correlationMessage,
                                                                         @NotNull IdsId connectorId) {
        var messageId = getMessageId();

        var builder = new RejectionMessageBuilder(messageId)
                ._contentVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(gregorianNow())
                ._issuerConnector_(connectorId.toUri())
                ._senderAgent_(connectorId.toUri());

        if (correlationMessage != null) {
            builder._correlationMessage_(correlationMessage.getId());
            builder._recipientAgent_(new ArrayList<>(Collections.singletonList(correlationMessage.getSenderAgent())));
            builder._recipientConnector_(new ArrayList<>(Collections.singletonList(correlationMessage.getIssuerConnector())));
        }

        return builder;
    }

    private static URI getMessageId() {
        return IdsId.Builder.newInstance().value(UUID.randomUUID().toString()).type(IdsType.MESSAGE).build().toUri();
    }
}
