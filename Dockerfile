FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew clean
# RUN ./gradlew sonarqube -Dsonar.projectKey=simulate-scf-service -Dsonar.host.url=https://sonar.akarinti.tech -Dsonar.login=dcf43cbde585144ebe5e1f3d98fccd275b20c296
RUN ./gradlew bootJar -x test --no-daemon

FROM openjdk:11-jre-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar","--spring.profiles.active=production,comprod"]
