@CALL "%PROGRAMFILES(X86)%\Embarcadero\RAD Studio\10.0\bin\rsvars.bat"

msbuild %2%

if %errorlevel% equ 0 EXIT