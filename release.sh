#!/bin/bash

echo "Re-building with target Java 7 (such that the compiled .class files will be compatible with as many JVMs as possible)..."

cd src

# build build build!
javac -encoding utf8 -d ../bin -bootclasspath ../other/java7_rt.jar -source 1.7 -target 1.7 @sourcefiles.list

cd ..



echo "Creating the release file pdfQrReader.zip..."

mkdir release

cd release

mkdir pdfQrReader

# copy the main files
cp -R ../bin pdfQrReader
cp ../UNLICENSE pdfQrReader
cp ../README.md pdfQrReader
cp ../run.sh pdfQrReader
cp ../run.bat pdfQrReader

# convert \n to \r\n for the Windows files!
cd pdfQrReader
awk 1 ORS='\r\n' run.bat > rn
mv rn run.bat
cd ..

# create a version tag right in the zip file
cd pdfQrReader
version=$(./run.sh version_for_zip)
echo "$version" > "$version"
cd ..

# zip it all up
zip -rq pdfQrReader.zip pdfQrReader

mv pdfQrReader.zip ..

cd ..
rm -rf release

echo "The file pdfQrReader.zip has been created in $(pwd)"

