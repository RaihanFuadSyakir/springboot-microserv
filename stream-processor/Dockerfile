FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/stream-processor-*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]