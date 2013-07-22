@echo off
@echo automatic build up a simple test environment in %cd%test

setlocal

set TEST_FILE_SOURCE=%~f0
md test
cd test
copy %TEST_FILE_SOURCE% test_file > nul
copy test_file test_file_readonly > nul
attrib +r test_file_readonly
FOR /L %%G IN (1,1,10) DO (
	md dir%%G
	copy test_file dir%%G > nul
)

endlocal