# meta-csgames-2024
This repository contains all the necessary files to build the firmware images for the IoT competition of the 2024 CSGames.

There are 2 images:
- The image for the players contains only one flag, which they get for successfully extracting the filesystem. This is the one that the players reverse engineer.
- The image with all the flags which has to be put on a real device. This is the one that get exploited with the knowledge aquired.

The images are built for the Raspberry Pi 3.

## How to build
This repository uses [kas](https://kas.readthedocs.io/en/latest/index.html) to manage the layers. You can run it however you want, including by using the provided Dockerfile.
~~~sh
docker build -t kas .
./run_docker.sh
~~~

Then, you can build either one of the images with its corresponding kas file.
~~~sh
kas build kas_csgames_iot_with_flags.yml
# or
kas build kas_csgames_iot_for_players.yml
~~~
The resulting `.rpi-sdimg` images will be located in `build/tmp/deploy/images/raspberrypi3`.
