FROM openjdk:17-jdk-slim AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/forex-application-0.0.1-SNAPSHOT.jar forex-application.jar
COPY src/main/resources/static /app/static
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=30s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1
CMD ["java", "-jar", "forex-application.jar"]
