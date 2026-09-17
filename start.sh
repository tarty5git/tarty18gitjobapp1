#!/bin/bash
# Linux Startup Script for LinkedIn Job Application Bot
# Package: com.cth.sdm

APP_JAR="target/linkedin-job-bot-1.0.0.jar"
PID_FILE="app.pid"
LOG_FILE="app.log"
DB_PROFILE="${SPRING_PROFILES_ACTIVE:-h2}"

echo "=========================================================="
echo " Starting LinkedIn Job Application Bot (Profile: $DB_PROFILE)"
echo "=========================================================="

# Build application if JAR does not exist
if [ ! -f "$APP_JAR" ]; then
    echo "JAR file not found. Building project with Maven..."
    mvn clean package -DskipTests
fi

# Check if application is already running
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "LinkedIn Job Bot is already running with PID $PID."
        exit 1
    else
        rm "$PID_FILE"
    fi
fi

# Run Spring Boot Application in background
nohup java -jar -Dspring.profiles.active="$DB_PROFILE" "$APP_JAR" > "$LOG_FILE" 2>&1 &
PID=$!
echo $PID > "$PID_FILE"

echo "LinkedIn Job Bot started successfully with PID $PID."
echo "Access Dashboard: http://localhost:8080"
echo "Access Swagger UI: http://localhost:8080/swagger-ui.html"
echo "Logs stream: tail -f $LOG_FILE"
