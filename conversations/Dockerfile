FROM amazoncorretto:21-alpine-jdk

WORKDIR /app 

COPY .mvn/ .mvn

COPY mvnw mvnw

COPY pom.xml pom.xml

RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
