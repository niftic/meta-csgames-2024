FROM crops/poky:ubuntu-22.04

USER root

RUN apt-get update &&\
    apt-get install -y python3-pip

RUN python3 -m pip install kas
