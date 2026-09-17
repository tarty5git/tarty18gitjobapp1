@echo off
REM Windows Startup Script for LinkedIn Job Application Bot
REM Package: com.cth.sdm

set APP_JAR=target\linkedin-job-bot-1.0.0.jar
if "%SPRING_PROFILES_ACTIVE%"=="" (
    set DB_PROFILE=h2
) else (
    set DB_PROFILE=%SPRING_PROFILES_ACTIVE%
)

echo ==========================================================
echo  Starting LinkedIn Job Application Bot (Profile: %DB_PROFILE%)
echo ==========================================================

IF NOT EXIST "%APP_JAR%" (
    echo JAR file not found. Building project with Maven...
    call mvn clean package -DskipTests
)

echo Starting Spring Boot Application in new window...
start "LinkedIn Job Application Bot" javaw -jar -Dspring.profiles.active=%DB_PROFILE% "%APP_JAR%"

echo LinkedIn Job Bot started successfully!
echo Access Dashboard: http://localhost:8080
echo Access Swagger UI: http://localhost:8080/swagger-ui.html
pause
