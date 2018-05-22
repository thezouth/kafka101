FROM ubuntu:16.04

RUN apt-get update \
    && apt-get install --no-install-recommends -y vim curl jq software-properties-common build-essential \
    && add-apt-repository ppa:jonathonf/python-3.6 \
    && apt-get remove -y --purge software-properties-common \
    && apt-get update \
    && apt-get install --no-install-recommends -y python3.6 python3.6-dev \
    && ln -sf /usr/bin/python3.6 /usr/bin/python3 \
    && ln -sf /usr/bin/python3.6 /usr/bin/python \
    && apt-get install --no-install-recommends -y python3-distutils python3-pip python3-setuptools \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install -U pip wheel setuptools
RUN SANIC_NO_UVLOOP=true SANIC_NO_UJSON=true pip3 install -U confluent-kafka httpie flask sanic


ENV PYTHONENCODING=utf-8

WORKDIR /workspace
