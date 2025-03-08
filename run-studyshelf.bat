@echo off
echo Starting StudyShelf application...
echo Make sure VcXsrv (XLaunch) is running!

REM Get the project root directory
SET PROJECT_ROOT=%~dp0
SET PROJECT_ROOT=%PROJECT_ROOT:~0,-1%

REM Build the credentials path from the project root
SET CREDS_PATH=%PROJECT_ROOT%\src\main\resources\credentials

REM Use the Downloads folder or another folder of your choice
SET DOWNLOADS_PATH=%USERPROFILE%\Downloads

docker run -v "%CREDS_PATH%:/app/credentials" -v "%DOWNLOADS_PATH%:/app/downloads" -e DISPLAY=host.docker.internal:0 studyshelf:latest

echo Docker container started with credentials from: %CREDS_PATH%
