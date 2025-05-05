@echo off
echo Starting StudyShelf application with database from Docker Hub...
echo Make sure VcXsrv (XLaunch) is running!

REM Create local directories for persistence
if not exist "data\mysql" mkdir data\mysql
if not exist "META-INF" mkdir META-INF

REM Create a custom persistence.xml file with the correct database host
echo ^<?xml version="1.0" encoding="UTF-8" standalone="yes"?^> > META-INF\persistence.xml
echo ^<persistence xmlns="https://jakarta.ee/xml/ns/persistence" >> META-INF\persistence.xml
echo              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >> META-INF\persistence.xml
echo              version="3.0" >> META-INF\persistence.xml
echo              xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"^> >> META-INF\persistence.xml
echo. >> META-INF\persistence.xml
echo     ^<persistence-unit name="studyshelf-test" transaction-type="RESOURCE_LOCAL"^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.User^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.StudyMaterial^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Category^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Tag^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Role^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Permission^</class^> >> META-INF\persistence.xml
echo         ^<properties^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.user" value="sa"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.password" value=""/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.hbm2ddl.auto" value="create-drop"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.show_sql" value="true"/^> >> META-INF\persistence.xml
echo         ^</properties^> >> META-INF\persistence.xml
echo     ^</persistence-unit^> >> META-INF\persistence.xml
echo     ^<persistence-unit name="StudyShelf" transaction-type="RESOURCE_LOCAL"^> >> META-INF\persistence.xml
echo         ^<provider^>org.hibernate.jpa.HibernatePersistenceProvider^</provider^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.User^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.StudyMaterial^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Category^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Tag^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Role^</class^> >> META-INF\persistence.xml
echo         ^<class^>domain.model.Permission^</class^> >> META-INF\persistence.xml
echo         ^<properties^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.url" value="jdbc:mariadb://db:3306/StudyShelf"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.user" value="appuser"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.password" value="password"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.pool_size" value="100"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.release_mode" value="after_transaction"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.lock.timeout" value="1000"/^> >> META-INF\persistence.xml
echo             ^<property name="jakarta.persistence.jdbc.driver" value="org.mariadb.jdbc.Driver"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.dialect" value="org.hibernate.dialect.MariaDBDialect"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.show_sql" value="true"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.hbm2ddl.auto" value="update"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.characterEncoding" value="utf8mb4"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.useUnicode" value="true"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.CharSet" value="utf8mb4"/^> >> META-INF\persistence.xml
echo             ^<property name="hibernate.connection.collation" value="utf8mb4_unicode_ci"/^> >> META-INF\persistence.xml
echo         ^</properties^> >> META-INF\persistence.xml
echo     ^</persistence-unit^> >> META-INF\persistence.xml
echo ^</persistence^> >> META-INF\persistence.xml

REM Create initialization SQL file
echo CREATE DATABASE IF NOT EXISTS StudyShelf; > init.sql
echo USE StudyShelf; >> init.sql

REM Create Docker override file with direct persistence.xml mount
echo version: '3.8' > docker-compose.override.yml
echo services: >> docker-compose.override.yml
echo   app: >> docker-compose.override.yml
echo     command: /bin/sh -c "echo 'Waiting for database to initialize...' && sleep 15 && echo 'Starting application with DB_HOST=db' && java -Djakarta.persistence.jdbc.url=jdbc:mariadb://db:3306/StudyShelf -Djakarta.persistence.jdbc.user=appuser -Djakarta.persistence.jdbc.password=password --module-path /app/javafx-libs/javafx-controls/20.0.1:/app/javafx-libs/javafx-graphics/20.0.1:/app/javafx-libs/javafx-base/20.0.1:/app/javafx-libs/javafx-fxml/20.0.1 --add-modules javafx.controls,javafx.fxml -jar studyshelf.jar" >> docker-compose.override.yml
echo     volumes: >> docker-compose.override.yml
echo       - ./META-INF/persistence.xml:/app/src/main/resources/META-INF/persistence.xml >> docker-compose.override.yml
echo       - ./src/main/resources/credentials:/app/src/main/resources/credentials >> docker-compose.override.yml
echo       - %USERPROFILE%/Downloads:/app/downloads >> docker-compose.override.yml
echo   db: >> docker-compose.override.yml
echo     volumes: >> docker-compose.override.yml
echo       - ./data/mysql:/var/lib/mysql >> docker-compose.override.yml
echo       - ./init.sql:/docker-entrypoint-initdb.d/init.sql >> docker-compose.override.yml
echo     networks: >> docker-compose.override.yml
echo       default: >> docker-compose.override.yml
echo         aliases: >> docker-compose.override.yml
echo           - localhost >> docker-compose.override.yml

REM Pull latest images
docker-compose pull

REM Launch the application with database
docker-compose up

pause

REM Clean up temporary files
del docker-compose.override.yml
del init.sql
REM Keep the META-INF directory for future use
