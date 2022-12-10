FROM --platform=amd64 openjdk:11

ENV JAVA_TOOL_OPTIONS "-Xms16G -Xmx30G"

WORKDIR /app
COPY web-application.jar app.jar

CMD ["java", "-jar", "app.jar"]