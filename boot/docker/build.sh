#! /bin/bash -e

rm -fr build
mkdir build
cp ../build/libs/boot-0.0.1.jar build

docker build -t authnet_service .