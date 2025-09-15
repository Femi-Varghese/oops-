@echo off
echo Vehicle Rental System - Compilation and Execution Script
echo ========================================================

echo.
echo Step 1: Compiling Java files...
javac -cp "mysql-connector-java-8.0.33.jar" *.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Please ensure:
    echo 1. MySQL connector JAR file is present in the directory
    echo 2. All Java files are in the same directory
    echo 3. Java JDK is properly installed
    pause
    exit /b 1
)

echo.
echo Step 2: Compilation successful!
echo.
echo Choose an option:
echo 1. Run Database Version (VehicleRentalAppWithDB)
echo 2. Run Original In-Memory Version (VehicleRentalApp)
echo 3. Initialize Database Only
echo 4. Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo.
    echo Starting Database Version...
    echo Make sure MySQL server is running!
    java -cp ".;mysql-connector-java-8.0.33.jar" VehicleRentalAppWithDB
) else if "%choice%"=="2" (
    echo.
    echo Starting Original Version...
    java VehicleRentalApp
) else if "%choice%"=="3" (
    echo.
    echo Initializing Database...
    java -cp ".;mysql-connector-java-8.0.33.jar" DatabaseInitializer
) else if "%choice%"=="4" (
    echo.
    echo Exiting...
    exit /b 0
) else (
    echo.
    echo Invalid choice. Please run the script again.
)

echo.
pause
