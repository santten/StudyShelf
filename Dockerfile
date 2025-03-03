FROM maven:3.9-amazoncorretto-17

LABEL authors="Jiayue, Santtu, Armas"

# required graphics libraries for JavaFX
RUN yum update -y && yum install -y \
    libX11 \
    libXext \
    libXrender \
    libXtst \
    mesa-libGL \
    gtk3 \
    xorg-x11-server-Xorg \
    && yum clean all

WORKDIR /app

COPY pom.xml /app/

COPY . /app/

RUN mvn package

CMD ["java", "-jar", "target/studyshelf.jar"]
