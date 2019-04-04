@echo off
title grounding routine
cd /d %~dp0
cd C:\Users\Kadiray\work_proseco_int\game\ga/
ga-server-event-driven config/server.kobo.conf >> Kobo.log 2>&1
