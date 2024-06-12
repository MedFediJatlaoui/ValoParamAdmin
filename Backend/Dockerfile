FROM amazoncorretto:17
ARG JAR_URL
EXPOSE 8090
ADD target/ValoParamAdmin-1.0.1.jar ValoParamAdmin-1.0.jar
#ADD $JAR_URL ValoParamAdmin-1.0.jar
CMD ["java", "-jar", "ValoParamAdmin-1.0.jar"]
