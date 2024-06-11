FROM openjdk:17-jdk

WORKDIR /app

COPY target/notification_service-0.0.1-SNAPSHOT.jar /app/pro.jar

COPY uploads/ /app/uploads/

EXPOSE 8080

CMD ["java", "-jar", "pro.jar"]
