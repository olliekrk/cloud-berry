#!/usr/bin/bash
docker build -t cloudberry-ng:latest .
docker tag cloudberry-ng:latest olliekrk/cloudberry-ng:latest
docker push olliekrk/cloudberry-ng:latest
