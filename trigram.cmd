@REM launcher script
@REM Shi.Zhan @ 2013
@REM use .classpath to load dependencies
@REM use absolute directory to load compiled classes
@echo off
setlocal
set CP=
set TGM_ROOT=%~dp0
for /f delims^=^"^ tokens^=2 %%i in ('find "lib" "%TGM_ROOT%.classpath"') do call :concat %%i
scala -cp "%CP%;%TGM_ROOT%target/scala-2.10/classes" Trigram %1 %2 %3 %4 %5 %6 
endlocal
goto :eof

:concat
set CP=%CP%%1;
goto :eof
