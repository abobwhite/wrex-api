FROM gradle:jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build

FROM openjdk:8-jre-alpine
COPY --from=build /home/gradle/src/build/libs/wrex-api.jar .
ENTRYPOINT ["java", "-XX:+UseStringDeduplication", "-XX:+UseG1GC", "-jar", "wrex-api.jar"]
