@ECHO OFF
@CALL "%PROGRAMFILES(X86)%\Embarcadero\RAD Studio\10.0\bin\rsvars.bat"

if not exist logs mkdir logs

@ECHO ON
msbuild %2 /p:config="%1"

if %errorlevel% equ 0 EXIT
if %errorlevel% neq 0 EXIT \B 1