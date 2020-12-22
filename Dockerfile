FROM java:8-jre-alpine
COPY web-api/target/web-api.jar /app/web-api.jar
WORKDIR /app
EXPOSE 8011
CMD ["java", "-jar","-Duser.timezone=GMT+08", "-Xms2048m", "-Xmx2048m", "web-api.jar"]