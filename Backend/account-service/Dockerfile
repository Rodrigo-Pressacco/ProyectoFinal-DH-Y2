#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim as base
COPY src /home/account-service/src
COPY pom.xml .
RUN mvn -f pom.xml clean package
COPY /account-service/target/account-service-*.jar /app.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app.jar"]

# FROM maven:3.6.0-jdk-11-slim as base
# WORKDIR /account-service
# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./
# RUN ./mvnw dependency:resolve
# COPY src ./src

# FROM base as development


# FROM base as build
# RUN ./mvnw package

# FROM alpine as production
# EXPOSE 8085
# COPY --from=build /account-service/target/account-service-*.jar /account-service.jar
# CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/account-service.jar"]