@echo off
title Campus Premier League (CPL) Simulator
echo ======================================================================
echo  Starting Campus Premier League (CPL) Simulator
echo  Vardhaman College of Engineering (VCE-R25) - Batch 6
echo ======================================================================
cd /d "%~dp0Course_End_Project_Java_Campus_Premier_League\app"
java -jar "target\CampusPremierLeague.jar"
if %errorlevel% neq 0 (
    echo.
    echo Application exited with an error.
    pause
)
