@set WDIR=%~dp0 
@scala -cp "commons-cli-1.2.jar;jena-tdb-0.9.4.jar;slf4j-api-1.7.2.jar;slf4j-simple-1.7.2.jar;%WDIR%" trigram %1 %2 %3 %4 %5 %6 
