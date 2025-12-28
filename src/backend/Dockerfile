FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
COPY resident_fee-service ./resident_fee-service

RUN mvn -pl package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/resident_fee-service/target/quarkus-app/ ./

EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]

