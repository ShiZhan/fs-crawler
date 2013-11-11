@REM launcher script
@REM Shi.Zhan @ 2013
@REM use 'sbt copy-dependencies' to collect dependencies
@REM use absolute directory to load compiled classes
@echo off
setlocal
@REM for translating big in-memory models
set JAVA_OPTS=%JAVA_OPTS% -Xmx1g
set CP=
set TGM_ROOT=%~dp0
for /f %%i in ('dir /b %TGM_ROOT%target\scala-2.10\lib') do call :concat %%i
scala -cp "%CP%;%TGM_ROOT%;%TGM_ROOT%target/scala-2.10/classes" TrigramTranslator %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
goto :eof

:concat
set CP=%CP%%TGM_ROOT%target/scala-2.10/lib/%1;
goto :eof