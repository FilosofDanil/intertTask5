FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/notification_service.jar notification_service.jar
ENTRYPOINT ["java","-jar","/notification_service.jar"]
