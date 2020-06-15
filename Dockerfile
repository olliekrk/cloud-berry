FROM openjdk:14-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod,mongo","/app.jar"]
