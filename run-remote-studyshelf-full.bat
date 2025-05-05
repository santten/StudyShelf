@echo off
echo Starting StudyShelf application with database from Docker Hub...
echo Make sure VcXsrv (XLaunch) is running!

REM Create a Docker-specific .env file
echo DB_HOST=db > .env.docker
echo DB_USER=appuser >> .env.docker
echo DB_PASSWORD=password >> .env.docker
echo DB_ROOT_PASSWORD=6661507 >> .env.docker
echo DOWNLOADS_DIR=/app/downloads >> .env.docker

REM Pull latest images
docker-compose pull

REM Launch the application with database using the Docker-specific .env file
docker-compose --env-file .env.docker up

pause
