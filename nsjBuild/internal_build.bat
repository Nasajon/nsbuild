@CALL "%PROGRAMFILES(X86)%\Embarcadero\RAD Studio\10.0\bin\rsvars.bat"

msbuild %2 /p:config="%1%"

if %errorlevel% equ 0 EXIT
if %errorlevel% neq 0 ABACATE
