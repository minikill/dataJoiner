FROM openjdk:8-jre-alpine

COPY target/dataJoiner-1.0-SNAPSHOT-jar-with-dependencies.jar /app/dataJoiner.jar
RUN apk update && \
    apk add --no-cache libc6-compat && \
    ln -s /lib/libc.musl-x86_64.so.1 /lib/ld-linux-x86-64.so.2
ENTRYPOINT ["java"]
CMD ["-Dcom.sun.management.jmxremote.port=5555", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-jar", "/app/dataJoiner.jar"]