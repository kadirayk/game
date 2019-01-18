FROM openjdk

COPY client client

COPY strategies strategies

COPY deployment.jar .

ENTRYPOINT ["java", "-jar", "/deployment.jar"]