FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

RUN apk add --no-cache maven

RUN mvn -q clean package -DskipTests

CMD ["java", "-cp", "target/classes", "com.seek.notifications.examples.NotificationExamples"]
