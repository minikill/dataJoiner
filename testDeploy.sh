#!/bin/bash

mvn clean install
docker cp target/dataJoiner-1.0-SNAPSHOT-jar-with-dependencies.jar sandbox-hdp:/tmp/dataJoiner.jar

