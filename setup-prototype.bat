echo setting up OTF-Gaming Prototype

echo PROSECO_DIR=%1

echo building jars
call mvn clean install -P core


mkdir -p %1/domains

ren .\target\game-0.0.1-SNAPSHOT.jar deployment.jar
echo f | xcopy /Y .\target\deployment.jar .\sample\game\prototypes\game\deployment.jar
echo f | xcopy /Y .\target\strategy.jar .\sample\game\prototypes\game\strategies\strategy1\strategy.jar
echo f | xcopy /Y .\target\grounding.jar .\sample\game\prototypes\game\grounding.jar

echo f | xcopy /Y .\sample\game\* %1\domains\game /s /i