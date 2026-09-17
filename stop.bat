@echo off
REM Windows Shutdown Script for LinkedIn Job Application Bot
REM Package: com.cth.sdm

echo ==========================================================
echo  Stopping LinkedIn Job Application Bot
echo ==========================================================

for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo Terminating process %%a on port 8080...
    taskkill /F /PID %%a
)

echo LinkedIn Job Bot stopped.
pause
