FROM amazoncorretto:17.0.6-alpine

ENV APP_HOME=/app
ENV JAVA_OPTS="-Xms128m -Xmx256m"
WORKDIR $APP_HOME

COPY build/libs/elo-0.0.1-SNAPSHOT.jar $APP_HOME/elo-0.0.1.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar elo-0.0.1.jar
