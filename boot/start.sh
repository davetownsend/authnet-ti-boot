#!/bin/bash

export JAVA_HOME={path to java home}  # TODO
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib

java -jar boot-0.0.1.jar --server.port=8282 &