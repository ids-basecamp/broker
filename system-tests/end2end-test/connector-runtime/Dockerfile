# -buster is required to have apt available
FROM openjdk:17-slim-buster

# Optional JVM arguments, such as memory settings
ARG JVM_ARGS=""

# Install curl, then delete apt indexes to save image space
RUN apt update \
    && apt install -y curl \
    && rm -rf /var/cache/apt/archives /var/lib/apt/lists

WORKDIR /app

COPY ./build/libs/app.jar /app

EXPOSE 8181
EXPOSE 9191
EXPOSE 8282

# health status is determined by the availability of the /health endpoint
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl --fail http://localhost:8181/api/check/health

ENV WEB_HTTP_PORT="8181"
ENV WEB_HTTP_PATH="/api"
ENV WEB_HTTP_MANAGEMENT_PORT="9191"
ENV WEB_HTTP_MANAGEMENT_PATH="/api/v1/management"
ENV WEB_HTTP_IDS_PORT="8282"
ENV WEB_HTTP_IDS_PATH="/api/v1/ids"

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS
ENV APPINSIGHTS_AGENT_VERSION=$APPINSIGHTS_AGENT_VERSION
ENTRYPOINT [ "sh", "-c", "exec java $JVM_ARGS -jar app.jar"]
