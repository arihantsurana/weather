#!/usr/bin/env bash
#Spark submit
docker run \
  -ti \
  --rm \
  -v $PWD/data:/data \
  -p 5000-5010:5000-5010 \
  -e SCM_URL="https://github.com/arihantsurana/weather.git" \
  -e SCM_BRANCH="setup-weather-java" \
  -e SPARK_DRIVER_HOST="172.25.8.30" \
  -e MAIN_CLASS="com.arihantsurana.weather.WeatherGenerator" \
  -e APP_ARGS="" \
  tashoyan/docker-spark-submit:spark-2.2.0
