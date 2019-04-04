#!/bin/bash

cd `dirname "$0"`

java -classpath "`dirname "$0"`/bin" -Xms16m -Xmx2048m com.asofterspace.pdfQrReader.Main "$@"
