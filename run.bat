@echo off
title Badminton Association Management System
cd /d "%~dp0"

REM Compile (skip if already compiled)
if not exist "target\classes\com\badminton\ui\MainMenu.class" (
    echo [Build] Compiling...
    dir /s /b src\main\java\*.java > src_files.txt
    javac -encoding UTF-8 -cp "lib\sqlite-jdbc-3.42.0.0.jar" -d target\classes @src_files.txt
    del src_files.txt
    if errorlevel 1 (
        echo [Error] Compilation failed!
        pause
        exit /b 1
    )
    echo [Build] Done.
)

echo.
java -Dfile.encoding=GBK -cp "target\classes;lib\sqlite-jdbc-3.42.0.0.jar" com.badminton.ui.MainMenu
pause
