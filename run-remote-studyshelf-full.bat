@echo off
echo Starting StudyShelf application with database from Docker Hub...
echo Make sure VcXsrv (XLaunch) is running!

REM Set environment variables to override persistence.xml
set DB_HOST=db
set DB_USER=appuser
set DB_PASSWORD=password
set DB_ROOT_PASSWORD=6661507
set DOWNLOADS_DIR=%USERPROFILE%\Downloads

echo Using database host: %DB_HOST%
echo Using downloads directory: %DOWNLOADS_DIR%

REM Pull latest images
docker-compose pull

REM Launch the application with database
docker-compose up

pause
