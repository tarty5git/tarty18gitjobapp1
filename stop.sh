#!/bin/bash
# Linux Shutdown Script for LinkedIn Job Application Bot
# Package: com.cth.sdm

PID_FILE="app.pid"

echo "=========================================================="
echo " Stopping LinkedIn Job Application Bot"
echo "=========================================================="

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "Stopping LinkedIn Job Bot (PID: $PID)..."
        kill "$PID"
        rm "$PID_FILE"
        echo "LinkedIn Job Bot stopped successfully."
    else
        echo "Process $PID is not running. Cleaning stale PID file."
        rm "$PID_FILE"
    fi
else
    echo "No PID file found ($PID_FILE). Attempting to find process on port 8080..."
    PID=$(lsof -t -i:8080 2>/dev/null)
    if [ -n "$PID" ]; then
        kill "$PID"
        echo "Stopped process running on port 8080 (PID: $PID)."
    else
        echo "LinkedIn Job Bot does not appear to be running."
    fi
fi
