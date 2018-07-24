| component              | name                           | description                                                                                               | type    | default           | values                                           | effect | file                                                                                  |
|------------------------|--------------------------------|-----------------------------------------------------------------------------------------------------------|---------|-------------------|--------------------------------------------------|--------|---------------------------------------------------------------------------------------|
| core                   | proto                          | RTP protocol                                                                                              | String  | udp               | tcp,udp                                          | none   | common/server-common.conf                                                             |
| core                   | base-object                    | RTSP object name                                                                                          | String  | /desktop          | *                                                | none   | common/server-common.conf                                                             |
| core                   | title                          | RTSP object title                                                                                         | String  | Real-Time Desktop | *                                                | none   | common/server-common.conf                                                             |
| core                   | server-port                    | RTSP server port                                                                                          | int     | 8554              | min=0,max=65535                                  | none   | common/server-common.conf                                                             |
| Audio                  | audio-encoder                  | Audio encoder                                                                                             | String  | libmp3lame        | libmp3lame,libopus,pcm_s16le                     | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-decoder                  | Audio decoder                                                                                             | String  | mp3               | mp3,libopus,pcm_s16le                            | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-samplerate               | Audio sampling rate                                                                                       | int     | 44100             | 44100,48000                                      | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-bitrate                  | Audio encoding rate                                                                                       | int     | 128000            | 128000                                           | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-channels                 | Number of audio channels, must be greater than zero                                                       | int     | 2                 | 2                                                | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-codec-format             | Codec data format of each audio sample, e.g., unsigned 8-bit and double                                   | String  | s16p              | s16p,s16                                         | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-codec-channel-layout     | Codec layout of the audio channels, e.g., mono, stereo                                                    | String  | stereo            | stereo                                           | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-device-channel-layout    | Device layout of the audio channels                                                                       | String  | stereo            | stereo                                           | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Audio                  | audio-device-format            | Device data type of each audio sample                                                                     | String  | s16               | s16                                              | low    | common/audio-lame.conf,common/audio-opus.conf,audio-pcm.conf                          |
| Video                  | video-encoder                  | Video encoder                                                                                             | String  | libx264           | libx264,libvpx,libx265                           | low    | common/video-x264.conf,common/video-vpx.conf,video-x265.conf                          |
| Video                  | video-decoder                  | Video decoder                                                                                             | String  | h264_dxva2 h264   | h264_dxva2 h264,libvpx,hevc                      | low    | common/video-x264.conf,common/video-vpx.conf,video-x265.conf                          |
| Video                  | video-fps                      | Video frame rate                                                                                          | int     | 24                | min=1,max=60                                     | medium | common/video-x264.conf,common/video-vpx.conf,video-x265.conf                          |
| Video                  | video-renderer                 | Using hardware acceleration for video rendering or not                                                    | String  | hardware          | hardware,software                                | low    | common/video-x264.conf,common/video-vpx.conf,video-x265.conf                          |
| Video                  | video-specific[b]              | The desired bitrate for video encoder (unit: bps)                                                         | int     | 3000000           | 1,3000000                                        | high   | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[refs]           | Number of reference frames to consider for motion compensation                                            | int     | 1                 | 1                                                | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[me_method]      | Motion estimation algorithms                                                                              | String  | dia               | zero,full,epzs,esa,dia,log,phods,X1,hex,umh,iter | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[me_range]       | Search range of motion vectors                                                                            | int     | 16                | 16                                               | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[intra_referesh] | Use periodic insertions of intra blocks instead of keyframes                                              | int     | 1                 | 1                                                | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[g]              | Set the group of picture size                                                                             | int     | 48                | 48                                               | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[slices]         | Number of slices, used in parallelized encoding                                                           | int     | 4                 | 4                                                | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | video-specific[threads]        | Thread count                                                                                              | int     | 4                 | 4,6                                              | low    | common/video-x264-param.conf,common/video-vpx-param.conf,common/video-x265-param.conf |
| Video                  | filter-source-pixelformat      | The video source pixel format                                                                             | String  | rgba              | rgba,bgra                                        | low    | server.[gamename].conf                                                                |
| Controller             | control-enabled                | Enable controller or not                                                                                  | boolean | true              | true,false                                       | none   | common/controller.conf                                                                |
| Controller             | control-port                   | TCP/UDP port number for control messages                                                                  | int     | 8555              | min=0,max=65535                                  | none   | common/controller.conf                                                                |
| Controller             | control-proto                  | Transport protocol for transmitting control messages                                                      | String  | udp               | tcp,udp                                          | none   | common/controller.conf                                                                |
| GA-Server-Periodic     | find-window-name               | The name of the window to be captured                                                                     | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Periodic     | find-window-class              | The title of the window to be captured (Windows only)                                                     | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Periodic     | display                        | The X Window DISPLAY to capture (X Window environment only)                                               | String  | :0                | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Periodic     | enable-audio                   | Enable or disable audio modules                                                                           | boolean | true              | true,false                                       | low    | server.[gamename].conf                                                                |
| GA-Server-Periodic     | logfile                        | Log GA outputs into a specified file                                                                      | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | find-window-name               | The name of the window to be captured                                                                     | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | find-window-class              | The title of the window to be captured (Windows only)                                                     | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | display                        | The X Window DISPLAY to capture (X Window environment only)                                               | String  | :0                | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | enable-audio                   | Enable or disable audio modules                                                                           | boolean | true              | true,false                                       | low    | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | logfile                        | Log GA outputs into a specified file                                                                      | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | game-exe                       | The absolute path pointing to the game executable                                                         | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | game-dir                       | The working directory for the game to be invoked (used only when a certain working directory is required) | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | game-resolution                | The screen resolution to be captured (used only when automatic resolution determination is not working)   | String  | *                 | *                                                | medium | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | hook-type                      | The function hook to be used.                                                                             | String  | sdl               | d9,d10,d10.1,d11,dxgi,sdl                        | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | hook-method                    | Reserved                                                                                                  | String  | *                 | *                                                | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | hook-experimental              | Enable experimental codes in hook functions                                                               | boolean | false             | true,false                                       | none   | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | enable-server-rate-control     | Enable a token bucket rate controller to throttle video frame rate                                        | boolean | Y                 | Y,N                                              | low    | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | server-max-tokens              | Parameter for the token bucket rate controller                                                            | int     | 2                 | 2                                                | low    | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | server-num-token-to-fill       | Parameter for the token bucket rate controller                                                            | int     | 1                 | 1                                                | low    | server.[gamename].conf                                                                |
| GA-Server-Event-Driven | server-token-fill-interval     | Parameter for the token bucket rate controller (unit: micro-seconds)                                      | int     | 20000             | 20000,41667                                      | low    | server.[gamename].conf                                                                |