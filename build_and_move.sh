#!/bin/bash

cd flfm
mvn clean package
cp target/flfm_plugin.jar ../mm_install/ij/plugins/flfm_plugin.jar
cd ../mm_install/ij
./ImageJ