@echo off
echo Starting StudyShelf application with database from Docker Hub...
echo Make sure VcXsrv (XLaunch) is running!

REM Pull latest images
docker-compose pull

REM Launch the application with database
docker-compose up

pause