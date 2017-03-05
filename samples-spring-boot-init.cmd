echo OFF

SET CURRENT_DIRECTORY=%~dp0
SET WORKING_DIRECTORY=%~dp0..\..\..\git\samples-spring-boot

echo.
echo current directory %CURRENT_DIRECTORY%
echo working directory %WORKING_DIRECTORY%
echo.

cd %WORKING_DIRECTORY%
dir
pause 

echo.

call mvn clean install -DskipTests
echo.

pause