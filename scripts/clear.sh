#!/usr/bin/env bash
# Data clearing batch

# Clears up data every n seconds. Also checks the total view count.

n=10
i=0

echo "Clear is starting. I'll clear tables every $n seconds"

while true
do
  if [ $i -eq $n ];then
    i=$((i%n))

    echo "$(date +"%H:%M:%S") - Clearing up started!"
    curl -s -o /dev/null -X POST "http://localhost:8080/user/clear"
    echo "$(date +"%H:%M:%S") - Clearing up ended!"
  fi

  i=$((i+1))
  view_count=$(curl -s http://localhost:8080/user/view-counts)
  echo $(date +"%H:%M:%S")" - $view_count"
  sleep 1
done
