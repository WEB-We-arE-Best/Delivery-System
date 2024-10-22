#!/bin/bash

# 스크립트 실행 시 에러 발생 시 즉시 종료
set -eu

# Define variables
GRADLE_CMD="./gradlew"
BUILD_OPTS="--no-daemon -x test"

# Function to build a service
build_service() {
  local service=$1
  echo "Building ${service} service..."
  $GRADLE_CMD :"${service}:clean" :"${service}:build" $BUILD_OPTS
  echo "${service} service build completed!"
}

# Check if services were passed as arguments, otherwise use default
if [ "$#" -gt 0 ]; then
  services=("$@")
else
  # Default list of services
  services=(
    "eureka"
    "gateway"
    "user"
    "auth"
    "coupon"
    "store"
    "order"
    "delivery"
    "rider"
  )
fi


# Build all services
for service in "${services[@]}"; do
  build_service "$service"
done