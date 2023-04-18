# syntax=docker/dockerfile:1

FROM gradle:7-jdk11 AS build

# COPY --from=prepare /root/.m2 /root/.m2
COPY --chown=gradle:gradle . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN gradle build --no-daemon

# ---

# -buster is required to have apt available
FROM openjdk:17-slim-buster

# For graceful shutdown of the EDC
STOPSIGNAL SIGINT

# Optional JVM arguments, such as memory settings
ARG JVM_ARGS=""

# Install curl, then delete apt indexes to save image space
RUN apt update \
    && apt install -y curl \
    && rm -rf /var/cache/apt/archives /var/lib/apt/lists

WORKDIR /app

COPY --from=build /home/gradle/project/launchers/build/libs/fc.jar /app
# COPY ./connector/src/main/resources/logging.properties /app

EXPOSE 8181
#EXPOSE 9191
#EXPOSE 8282

# health status is determined by the availability of the /health endpoint
#ENV EDC_API_AUTH_KEY=""
# HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -H "X-Api-Key: $EDC_API_AUTH_KEY" --fail http://localhost:8181/api/check/health
HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl --fail http://localhost:8181/api/check/health

#ENV WEB_HTTP_PORT="8181"
#ENV WEB_HTTP_PATH="/api"
#ENV WEB_HTTP_DATA_PORT="9191"
#ENV WEB_HTTP_DATA_PATH="/api/v1/data"
#ENV WEB_HTTP_IDS_PORT="8282"
#ENV WEB_HTTP_IDS_PATH="/api/v1/ids"

# Use "exec" for graceful termination (SIGINT) to reach JVM.
# ARG can not be used in ENTRYPOINT so storing values in ENV variables
ENV JVM_ARGS=$JVM_ARGS
ENTRYPOINT [ "sh", "-c", \
    "exec java $JVM_ARGS -jar fc.jar"]
# "exec java -Djava.util.logging.config.file=/app/logging.properties $JVM_ARGS -jar mds_edc.jar"]

