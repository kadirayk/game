(
echo [core]
echo include = common/server-common.conf
echo include = common/controller.conf
echo include = common/video-x264.conf
echo include = common/video-x264-param.conf
echo include = common/audio-lame.conf
echo.
echo [video]
echo video-fps = 50
echo.
echo [filter]
echo filter-source-pixelformat = rgba
echo.
echo [ga-server-event-driven]
echo game-exe = %~dp0NeverballPortable\App\Neverball\neverball.exe
echo.
echo # hook configuration
echo # version: d9, d10, d10.1, d11, dxgi, sdl
echo hook-type = sdl
echo hook-audio = coreaudio
echo.
echo enable-audio = true
echo.
echo find-window-class = SDL_app
echo.
echo enable-server-rate-control = Y
echo server-token-fill-interval = 20000
echo server-num-token-to-fill = 1
echo server-max-tokens = 2
) > .\ga\config\server.neverball.conf


echo copying jar
echo f | xcopy /Y %~dp0..\..\target\server-rmi-server.jar server-rmi-server.jar

echo running rmi server
java -jar .\server-rmi-server.jar
