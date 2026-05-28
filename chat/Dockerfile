FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests
FROM amazoncorretto:21-alpine
COPY --from=build /app/target/*.jar /app.jar
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
