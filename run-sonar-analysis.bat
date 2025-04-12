@echo off
echo ===================================================
echo Starting SonarQube Analysis for StudyShelf
echo ===================================================
@echo off
echo ===================================================
echo Starting SonarQube Analysis for StudyShelf
echo ===================================================

REM Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Amazon Corretto\jdk17.0.14_7
echo Setting JAVA_HOME to: %JAVA_HOME%

REM Add Java to PATH
set PATH=%JAVA_HOME%\bin;%PATH%
REM Display current directory for debugging
echo Current directory: %CD%

REM Clean and prepare the project
echo.
echo Step 1: Cleaning and preparing the project...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo Maven clean failed with error code %ERRORLEVEL%
    goto error
)

REM Copy dependencies
echo.
echo Step 2: Copying dependencies...
call mvn dependency:copy-dependencies
if %ERRORLEVEL% neq 0 (
    echo Maven dependency:copy-dependencies failed with error code %ERRORLEVEL%
    goto error
)

REM Compile the project
echo.
echo Step 3: Compiling the project...
call mvn compile test-compile
if %ERRORLEVEL% neq 0 (
    echo Maven compile failed with error code %ERRORLEVEL%
    goto error
)

REM Run tests
echo.
echo Step 4: Running tests...
call mvn test
if %ERRORLEVEL% neq 0 (
    echo Maven test failed with error code %ERRORLEVEL%
    goto error
)

REM Run SonarQube analysis
echo.
echo Step 5: Running SonarQube analysis...
REM Run SonarScanner with the token from .env
REM Load variables from .env file
for /F "TOKEN=*" %%A in (.env) do set %%A
sonar-scanner -Dsonar.host.url=%SONAR_HOST_URL% -Dsonar.login=%SONAR_TOKEN%
if %ERRORLEVEL% neq 0 (
    echo SonarQube analysis failed with error code %ERRORLEVEL%
    goto error
)

echo.
echo ===================================================
echo SonarQube Analysis completed successfully!
echo ===================================================
echo Results available at: http://localhost:9000
goto end

:error
echo.
echo ===================================================
echo ERROR: SonarQube Analysis failed!
echo ===================================================

:end
echo.
echo Press any key to close this window...
pause > nul


