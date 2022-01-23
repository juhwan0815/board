FROM openjdk:17-ea-11-jdk-slim

VOLUME /tmp
COPY build/libs/board-0.0.1-SNAPSHOT.jar board.jar

ENTRYPOINT ["java","-jar","board.jar"]