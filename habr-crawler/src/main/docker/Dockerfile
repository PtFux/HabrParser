# Этап сборки с Maven
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# Копируем POM первым для кэширования
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Копируем исходники
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]