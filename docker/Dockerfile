###### PACKAGE ######
FROM maven:3.6.3-openjdk-14-slim AS build
RUN mkdir -p /home/app
WORKDIR /home/app
COPY pom.xml /home/app
RUN mvn dependency:go-offline dependency:resolve -B
COPY src /home/app/src
RUN mvn package -DskipTests



###### RUN ######
FROM openjdk:14-jdk-alpine
RUN mkdir -p /home/app/
WORKDIR /home/app
COPY --from=build /home/app/target/dependency/jetty-runner.jar /home/app/jetty-runner.jar
COPY --from=build /home/app/target/*.war /home/app/jetty-playground.war
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/app/jetty-runner.jar", "/home/app/jetty-playground.war"]
