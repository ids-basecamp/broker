package de.truzzt.edc.extension.broker.api.handler;

import de.truzzt.edc.extension.broker.api.message.MultipartRequest;
import de.truzzt.edc.extension.broker.api.message.MultipartResponse;
import org.jetbrains.annotations.NotNull;

public interface Handler {

    boolean canHandle(@NotNull MultipartRequest multipartRequest);

    @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest);
}
