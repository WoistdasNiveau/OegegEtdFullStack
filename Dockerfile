FROM openjdk:17-alpine

COPY target/oegegetdjava-1.0-SNAPSHOT.jar etd.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/etd.jar"]