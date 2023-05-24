FROM openjdk:21-jdk-oracle
COPY target/pokemon-service-fullstack-1.1.0.jar /app/pokemon-service-fullstack-1.1.0.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "pokemon-service-fullstack-1.1.0.jar"]
