# Etapa 1: compilar el proyecto de Spring Boot
FROM 3.9.1-amazoncorretto-8-debian
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:go-offline \
&& mvn package -Dmaven.compiler.target=20
COPY src/ /app/src/
RUN mvn package

# Etapa 2: ejecutar el archivo JAR en una imagen de JRE
FROM amazoncorretto:20.0.1
WORKDIR /app
COPY --from=build /app/target/serviceguideBackend-0.0.1-SNAPSHOT.jar /app/serviceguideBackend.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/serviceguideBackend.jar"]
