FROM eclipse-temurin:17-jre

WORKDIR /app

# Optional: install curl for runtime debugging
#RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates \
#    && rm -rf /var/lib/apt/lists/*

COPY resident_fee-service/target/quarkus-app/ ./

EXPOSE 8080

CMD ["java", "-jar", "quarkus-run.jar"]
#CMD ["sh", "-c", "java $JAVA_OPTS -jar quarkus-run.jar"]
