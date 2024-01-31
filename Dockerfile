FROM maven:3-amazoncorretto-17
LABEL authors="wissensalt"

WORKDIR .
COPY . .
RUN mvn clean package

CMD mvn spring-boot:run
