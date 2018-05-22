#!/bin/bash
docker run -it -v `pwd`/.:/workspace \
    --network documents_default \
    roongr2k7/python-kafka
