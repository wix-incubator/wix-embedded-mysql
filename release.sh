#!/usr/bin/env bash

mvn -Darguments="-DskipTests" release:clean release:prepare release:perform --settings ~/.m2/settings-deploy.xml
