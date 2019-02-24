echo building client-rmi-server.jar
call mvn clean install -P client

echo f | xcopy /Y .\target\client-rmi-server.jar .

echo starting client-rmi-server
java -jar .\client-rmi-server.jar %~dp0ga