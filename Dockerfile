FROM openjdk:21-jdk-oracle
ADD target/pokemon-service-fullstack-docker.jar .
EXPOSE 8080
CMD java -jar pokemon-service-fullstack-docker.jar
