#!/usr/bin/env bash
cd $(dirname $0)
java -Xmx16g -classpath GenEditScan.jar:libs/lib/*:libs/jfxConverter-0.24/distrib/svg/* GenEditScan.Main
