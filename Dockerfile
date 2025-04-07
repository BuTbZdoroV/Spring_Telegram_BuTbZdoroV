# Базовый образ с Java 17 (альтернатива: eclipse-temurin:17-jdk)
FROM openjdk:17-jdk-slim

# Рабочая директория в контейнере
WORKDIR /app

# Копируем JAR из папки сборки Gradle
COPY build/libs/zxcChatBuTb-0.0.1-SNAPSHOT.jar app.jar
COPY docker-compose.yml ./
# Открываем порт Spring Boot (по умолчанию 8080)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]