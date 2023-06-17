FROM gradle AS TEMP_BUILD_IMAGE
ARG APP_HOME=/usr/app
ENV APP_HOME=${APP_HOME}
WORKDIR $APP_HOME

# Add gradle & src code
ADD . $APP_HOME
# package jar
RUN gradle clean bootJar

# Second stage: minimal runtime environment
FROM arm64v8/openjdk:21
ARG APP_HOME=/usr/app
ENV APP_HOME=${APP_HOME}
ENV ARTIFACT_NAME=yoursell-0.0.1.jar
# copy jar from the first stage
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
ENTRYPOINT exec java -jar $ARTIFACT_NAME
EXPOSE 8080