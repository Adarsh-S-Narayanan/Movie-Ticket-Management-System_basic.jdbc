@echo off
set "LIB_DIR=d:\code\class\jdbc\lib"
set "CP=.;%LIB_DIR%\mysql-connector-j-9.2.0.jar;%LIB_DIR%\javafx-base-21.0.2-win.jar;%LIB_DIR%\javafx-base-21.0.2.jar;%LIB_DIR%\javafx-controls-21.0.2-win.jar;%LIB_DIR%\javafx-controls-21.0.2.jar;%LIB_DIR%\javafx-graphics-21.0.2-win.jar;%LIB_DIR%\javafx-graphics-21.0.2.jar"

echo Compiling Java source files...
javac --module-path "%LIB_DIR%" --add-modules javafx.controls,javafx.graphics -cp "%CP%" *.java

if %errorlevel% equ 0 (
    echo Launching CLI...
    java -cp "%CP%" MovieTicketManagementSystem
) else (
    echo Compilation failed.
)
pause
