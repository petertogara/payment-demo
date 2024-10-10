FROM ubuntu:latest
LABEL authors="petertogara"

FROM openjdk:17-jdk-slim

ENV SPRING_PROFILES_ACTIVE=dev

WORKDIR /app

# Accept the version argument from the build process
ARG JAR_FILE=payment-demo.jar

# Copy the built JAR file into the container (using the dynamic version)
COPY target/${JAR_FILE} /app/payment-demo.jar

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 CMD curl --fail http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/payment-demo.jar"]
