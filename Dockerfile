FROM maven:3.9.6-eclipse-temurin-17 AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -f pom.xml clean package

# Deploy stage: Use Tomcat 10+ with Java 17+
FROM tomcat:10.1-jdk17-temurin
RUN mkdir -p  /User/File/TimeSheet/files/
ENV CATALINA_OPTS="-Xms1G -Xmx1G"
COPY --from=build /workspace/target/*.war /usr/local/tomcat/webapps/mtimesheet.war
#COPY --from=buildui  usr/src/app/dist   /usr/local/tomcat/webapps/mc-ui
#COPY tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
#COPY context.xml /usr/local/tomcat/conf/context.xml


EXPOSE 8080
CMD ["catalina.sh", "run"]

#FROM openjdk:8-alpine
#COPY --from=build /workspace/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","app.jar"]
