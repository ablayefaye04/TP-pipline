# Image d'execution : on copie le JAR deja construit par Maven (principe "build once, deploy many")
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
