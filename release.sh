#!/usr/bin/env bash

mvn release:clean release:prepare release:perform --settings ~/.m2/settings-deploy.xml
