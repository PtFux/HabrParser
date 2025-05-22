# article-generator/Dockerfile
# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl article-generator -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/article-generator/target/article-generator-*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]