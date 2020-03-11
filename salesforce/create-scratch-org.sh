#!/usr/bin/env bash
mkdir -p tmp
sfdx force:org:delete -p -u ${SF_ALIAS}
sfdx force:org:create --setalias ${SF_ALIAS} -f ./config/project-scratch-def.json
sfdx force:source:push -u JolyDev
sfdx force:apex:execute -u JolyDev --json -f apex/setup.apex

## Command to get
sfdx force:org:display -u JolyDev --json > tmp/JolyDev.json


