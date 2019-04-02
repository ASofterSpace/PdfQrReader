@echo off

cd /D %~dp0

java -classpath "%~dp0\bin" -Xms16m -Xmx2048m com.asofterspace.pdfQrReader.Main %*

pause