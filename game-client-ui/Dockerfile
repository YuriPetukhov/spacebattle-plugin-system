FROM openjdk:19
WORKDIR /app
COPY target/auth-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
