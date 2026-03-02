@echo off
echo Compiling...

javac App.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation FAILED
    pause
    exit /b
)

echo Running program...
java App

pause