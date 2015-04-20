#!/bin/bash
# Simple Stress Tool

# The perfomance of this tool is vary depends on the machine it's running on.

N=300 # Requests per log(echo). Must not exceed the number of max user processes(`ulimit -u`).
RANGE=10000 # User ID range

echo "Stress is starting. User ID range from 1 to $RANGE"

i=0
cnt=0
while true
do
  if [ $i -eq $N ];then
    i=$((i%N))
    echo "`date +"%H:%M:%S"` - $cnt requests made"
  fi

  i=$((i+1))
  cnt=$((cnt+1))

  # $RANDOM returns a different random integer at each invocation.
  # Nominal range: 0 - 32767 (signed 16-bit integer).
  # http://tldp.org/LDP/abs/html/randomvar.html
  host_id=$(($RANDOM % RANGE + 1))
  visitor_id=$(($RANDOM % RANGE + 1))

  curl -s -o /dev/null -X POST "http://localhost:8080/user/$host_id?visitor_id=$visitor_id" &
  wait  # Need to wait to prevent processes from forking too much user processes
done
