@echo off
REM Proxy script to run the Android frontend's Gradle wrapper from the workspace root.
setlocal
set SCRIPT_DIR=%~dp0
call "%SCRIPT_DIR%android_frontend\gradlew.bat" %*
endlocal
