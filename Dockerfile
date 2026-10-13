# El JAR lo genera el job "build" del pipeline (mvn package) y se copia aqui.
# En local: ./mvnw clean package -DskipTests && docker build -t banco-udea .
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/bancoudea.jar app.jar
# En la nube se usa H2 en memoria (application-cloud.properties). Se puede sobreescribir con una variable de entorno.
ENV SPRING_PROFILES_ACTIVE=cloud
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
