@REM launcher script
@REM Shi.Zhan @ 2013
@echo off
for /f delims^=^"^ tokens^=2 %%i in ('find "lib" ..\.classpath') do call :concat %%i
scala -cp "%CP%;../target/scala-2.10/classes" trigram %1 %2 %3 %4 %5 %6 
goto :eof

:concat
set CP=%CP%%1;
goto :eof