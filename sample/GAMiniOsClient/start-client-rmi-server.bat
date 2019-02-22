echo f | xcopy %~dp0..\..\target\client-rmi-server.jar client-rmi-server.jar

java -jar .\client-rmi-server.jar %~dp0ga