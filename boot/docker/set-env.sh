#!/bin/bash

export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
export DOCKER_HOST_IP=$(boot2docker ip 2>/dev/null)