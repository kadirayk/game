@echo off
cd /d %~dp0
title Gaming Strategy
echo Starting execution of Strategy in %~dp0strategy.jar %1 %2 %3 %4
java -jar %~dp0strategy.jar %1 %2 %3 %4 131.234.250.150 1099 remote