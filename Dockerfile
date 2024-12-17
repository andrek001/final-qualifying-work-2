FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY target/cloud_service-0.0.1-SNAPSHOT.jar cloud_service.jar

CMD ["java", "-jar", "cloud_service.jar"]

