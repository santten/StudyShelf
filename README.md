### StudyShelf

Java-based Classroom Resource Platform

Group 6 at Metropolia UAS 2025

### Instructions

Install https://sourceforge.net/projects/vcxsrv/ (windows)
- Launch VcXsrv using the "XLaunch" application.
- Select "Multiple windows" and click "Next".
- Choose "Start no client" and click "Next".
- Ensure "Disable access control" is checked and click "Finish"

In terminal: set DISPLAY="Your-IP-Address":0.0

Create .env file in project root folder:

- DB_PASSWORD = "youruserDBpassword"
- DB_ROOT_PASSWORD = "yourDBrootpassword"
- DOWNLOADS_DIR=C:/Users/<yourusername>/Downloads
- or you can choose other folder. Folder will be shared with docker image to store dowloaded files and upload files.


Start Docker Desktop
 
Start run-remote-studyshelf-full.bat in project root folder.