FROM openjdk:15-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
