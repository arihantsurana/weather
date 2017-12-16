#!/usr/bin/env bash
# Setup local ip
# for linux keep using localhost
# export $LOCAL_IP=localhost
# for mac use the local ip of the machine
export LOCAL_IP=$(ipconfig getifaddr en0)

#Spark submit
# this will setup the docker container, download sbt the source,
# build it and then execute it for you
docker run \
  --name weather-spark \
  -ti \
  --rm \
  -v $PWD/data:/data \
  -p 5000-5010:5000-5010 \
  -e SCM_URL="https://github.com/arihantsurana/weather.git" \
  -e SPARK_DRIVER_HOST="${LOCAL_IP}" \
  -e MAIN_CLASS="com.arihantsurana.weather.WeatherGenerator" \
  -e APP_ARGS="100 result.files" \
  tashoyan/docker-spark-submit:spark-2.2.0

