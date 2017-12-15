#!/usr/bin/env bash
# Setup local ip
# for mac use
# ipconfig getifaddr en0
# to get the local ip
# for linux keep local host
# export $LOCAL_IP=localhost
export LOCAL_IP=172.25.8.30
#Spark submit
docker run \
  --name weather-spark \
  -ti \
  --rm \
  -v $PWD/data:/data \
  -p 5000-5010:5000-5010 \
  -e SCM_URL="https://github.com/arihantsurana/weather.git" \
  -e SPARK_DRIVER_HOST="${LOCAL_IP}" \
  -e MAIN_CLASS="com.arihantsurana.weather.WeatherGenerator" \
  -e APP_ARGS="10000 result.files" \
  tashoyan/docker-spark-submit:spark-2.2.0

# docker rm weather-spark -f
