FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY pom.xml .
RUN mvn clean install -DskipTests
COPY target/forex-application-0.0.1-SNAPSHOT.jar forex-application.jar
EXPOSE 8080
CMD ["java", "-jar", "forex-application.jar"]