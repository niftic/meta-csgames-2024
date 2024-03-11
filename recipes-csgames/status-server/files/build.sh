#!/bin/sh
mvn package && \
    mv target/client.jar . && \
    mv target/server.jar . && \
    mvn clean
