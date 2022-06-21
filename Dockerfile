FROM openjdk:11-jdk
EXPOSE 8080
ADD ./build/libs/foodduck-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=staging", "app.jar"]