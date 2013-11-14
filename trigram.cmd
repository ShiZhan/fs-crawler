@REM launcher script
@REM Shi.Zhan @ 2013
@REM use 'sbt copy-dep' to collect dependencies in "target\scala-2.10\lib"
@REM load compiled classes from "target\scala-2.10" directly for development
@REM if deploy with individual "jar" libs, update "set LIB" to lib directory.
@echo off
setlocal

set JAVA_OPTS=%JAVA_OPTS% -Xmx2g
set CP=
set TGM_ROOT=%~dp0
@REM set TGM_DATA=r:/data
set LIB=%TGM_ROOT%target\scala-2.10\lib\
for /f %%i in ('dir /b %LIB%') do call :concat %%i
scala -cp "%CP%;%TGM_ROOT%;%TGM_ROOT%target/scala-2.10/classes" Trigram %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
goto :eof

:concat
set CP=%CP%%LIB%%1;
goto :eof
