FROM openjdk:8-jre-alpine

COPY target/dataJoiner-1.0-SNAPSHOT-jar-with-dependencies.jar /app/dataJoiner.jar
ENTRYPOINT ["java"]
CMD ["-Dcom.sun.management.jmxremote.port=5555", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-jar", "/app/dataJoiner.jar"]