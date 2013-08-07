@REM launcher script
@REM Shi.Zhan @ 2013
@REM use .classpath to load dependencies
@REM use absolute directory to load compiled classes
@echo off
setlocal
@REM for translating big in-memory models
set JAVA_OPTS=%JAVA_OPTS% -Xms1024m -Xmx1024m

set CP=
set TGM_ROOT=%~dp0
for /f delims^=^"^ tokens^=4 %%i in ('find "lib" "%TGM_ROOT%.classpath"') do call :concat %%i
scala -cp "%CP%;%TGM_ROOT%;%TGM_ROOT%target/scala-2.10/classes" TrigramThinker %1 %2 %3 %4 %5 %6 
endlocal
goto :eof

:concat
set CP=%CP%%1;
goto :eof
