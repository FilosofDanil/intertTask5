FROM openjdk:17-jdk

WORKDIR /app

COPY .env .env

COPY target/notification_service-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
