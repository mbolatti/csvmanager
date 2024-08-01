FROM openjdk:17-jdk-slim
COPY /web/target/web-0.0.1-SNAPSHOT.jar app.jar
#COPY /home/mbolatti/Documentos/colloquios/neosperience/csvmanager/web/target/web-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
