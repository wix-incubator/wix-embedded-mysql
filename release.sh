#!/usr/bin/env bash

mvn -Dmaven.test.skip=true release:clean release:prepare release:perform --settings ~/.m2/settings-deploy.xml
