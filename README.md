# StudyShelf
## Introduction
The project is a Java-based Classroom Resource Sharing Platform designed to become a platform that teachers and students can use to easily share useful educational resources for their studies. The platform aims to enhance learning efficiency and resource accessibility in educational environments, making it easier for teachers and students to collaborate and access useful study materials.  
  
Developed by Group 6 at Metropolia UAS, 2025.  
  
## Instructions

Install https://sourceforge.net/projects/vcxsrv/ (windows)
- Launch VcXsrv using the "XLaunch" application.
- Select "Multiple windows" and click "Next".
- Choose "Start no client" and click "Next".
- Ensure "Disable access control" is checked and click "Finish"


Group 6 at Metropolia UAS 2
=======
In terminal: set DISPLAY="Your-IP-Address":0.0

Create .env file in project root folder:

- DB_PASSWORD = "youruserDBpassword"  
- DB_ROOT_PASSWORD = "yourDBrootpassword"  
- DOWNLOADS_DIR=C:/Users/<yourusername>/Downloads  
- or you can choose other folder. Folder will be shared with docker image to store dowloaded files and upload files.  


Start Docker Desktop  
 
Start run-remote-studyshelf-full.bat in project root folder.  

