# Notification Service

## Launch

1. Clone repository:

    ```bash
    git clone https://github.com/FilosofDanil/intertTask5.git
    cd intertTask5
    ```

2. Create file `.env` in root and add your settings there, example:

    ```plaintext
    SPRING_MAIL_HOST=xxx.gmail.com
    SPRING_MAIL_USERNAME=xxxxxxxxx@gmail.com
    SPRING_MAIL_PASSWORD=xxxxxxxxxxx
    SPRING_MAIL_PORT=465
    SPRING_MAIL_PROTOCOL=xxxxxxx
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
    ```

3. Build project using Maven(Skip tests to build faster if needed):

    ```bash
    cd notification_service
    mvn clean package -DskipTests
    ```

4. Launch Docker Compose:

    ```bash
    docker-compose up --build
    ```
