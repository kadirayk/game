@echo off
cd /d %~dp0
title Grounding Gaming
echo Deploying Gaming Service
@start /b cmd /c java -jar %~dp0deployment.jar %1 %2 %3