FROM openjdk:21-jdk

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

COPY ./build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]


