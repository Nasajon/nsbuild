@ECHO OFF
@CALL "%PROGRAMFILES(X86)%\Embarcadero\RAD Studio\10.0\bin\rsvars.bat"

if not exist logs mkdir logs

msbuild "%2" /t:%4 /p:BuildInParallel=true /v:minimal /p:config="%1" > logs\%3.log

if %errorlevel% equ 0 EXIT
if %errorlevel% neq 0 EXIT \B 1
