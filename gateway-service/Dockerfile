FROM openjdk:19
WORKDIR /app
COPY target/gateway-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
