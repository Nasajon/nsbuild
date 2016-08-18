PUSHD "C:\@work\erp\source\desktop_new\_bin"
@CALL "copydfm.bat"
POPD

java -jar nsjBuild.jar %1 %2