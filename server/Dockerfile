FROM openjdk:19
WORKDIR /app
COPY server/target/server-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY plugins ./plugins
ENTRYPOINT ["java", "-jar", "app.jar"]
