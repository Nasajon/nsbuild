@ECHO OFF
PUSHD "C:\@work\erp\source\desktop_new\_bin"
@CALL "copydfm.bat"
POPD

@ECHO ON
java -jar nsjBuild.jar %1 %2 %3