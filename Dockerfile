# Build stage
FROM maven:3.9-amazoncorretto-17 AS build
LABEL authors="Jiayue, Santtu, Armas"
WORKDIR /app
COPY pom.xml .
COPY . .
ARG SKIP_CREDENTIALS=false
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
RUN mvn package -DskipTests

# Runtime stage
FROM amazoncorretto:17
WORKDIR /app
VOLUME /app/credentials

# Graphics libraries for JavaFX
RUN yum update -y && yum install -y \
    libX11 \
    libXext \
    libXrender \
    libXtst \
    mesa-libGL \
    gtk3 \
    xorg-x11-server-Xorg \
    && yum clean all
RUN if [ "$SKIP_CREDENTIALS" = "false" ]; then \
      mkdir -p /app/credentials; \
    fi


COPY --from=build /app/target/studyshelf.jar /app/
COPY --from=build /app/target/dependency /app/dependency
COPY --from=build /root/.m2/repository/org/openjfx /app/javafx-libs

# Display environment variable
ENV DISPLAY=host.docker.internal:0

# Run
CMD ["java", "--module-path", "/app/javafx-libs/javafx-controls/20.0.1:/app/javafx-libs/javafx-graphics/20.0.1:/app/javafx-libs/javafx-base/20.0.1:/app/javafx-libs/javafx-fxml/20.0.1", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "studyshelf.jar"]
