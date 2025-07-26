@echo off
echo Building Self-Organizing Map (SOM) Application...

:: Create bin directory if it doesn't exist
if not exist bin mkdir bin

:: Compile Java files
javac -d bin src\main\java\com\fabio\som\*.java

:: Check if compilation was successful
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.
echo To run the application, use:
echo java -cp bin com.fabio.som.SOMApplication data\test_data.csv
echo.

:: Ask if user wants to run the application
set /p RUN_APP="Run the application now? (y/n): "
if /i "%RUN_APP%"=="y" (
    echo.
    echo Running SOM Application...
    echo.
    java -cp bin com.fabio.som.SOMApplication data\test_data.csv
)