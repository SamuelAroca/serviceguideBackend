FROM maven:amazoncorretto AS build

RUN mvn clean
RUN mvn install


FROM amazoncorretto:20.0.1

COPY target/serviceguideBackend.jar serviceguideBackend.jar

ENTRYPOINT ["java", "-jar", "/serviceguideBackend.jar"]