# game
OTF-Gaming Prototype for [PROSECO](https://github.com/fmohr/PROSECO)

## Build
- JDK
- Maven

#### As PROSECO Prototype: 
- `setup-prototype.bat /path/to/PROSECO`

#### As RMI Client:
- set **client.rmi_server_ip** as your public IP in `rmi-server.properties`
- run `setup-rmi-client.bat`

#### As RMI Server:
- set **server.rmi_server_ip** as your public IP in `rmi-server.properties`
- Download [NeverballPortable](http://gaminganywhere.org/dl/games/NeverballPortable_1.5.4_Rev_2.paf.exe) and install into current directory
- set environmet variable **APPDATA** as C:\Users\\[username]\AppData\Roaming
- run `setup-rmi-server.bat`
