FROM gradle:jdk17 as build
COPY --chown=gradle:gradle . /home/gradle
WORKDIR /home/gradle
RUN gradle build

FROM openjdk:17-alpine AS deployment
RUN mkdir /app
COPY --from=build /home/gradle/build/libs/*.jar /app/springboot-app.jar
CMD java -Dserver.port=$PORT $JAVA_OPTS -jar /app/springboot-app.jar