#!/usr/bin/env bash

MAX_THREAD_COUNT=499
echo "Starting watchdog"
PID=`ps ax | grep "/home/vcap/app/.java-buildpack/spring" | awk '{print $1}' | sed -n 1p`
#echo "Found app PID: ${PID}"

while true; do

    #default to 8080 if port isn't set by CF
    PORT="${PORT:-8080}"

    #curl to metrics endpoint
    HTTP_STATUS=`curl --basic -u admin:I_LXH,#2dddq -s -m 1 -o /dev/null -w "%{http_code}"  http://127.0.0.1:${PORT}/metrics`
    EXIT_STATUS=$?

    echo "HTTP_STATUS: ${HTTP_STATUS}"
    echo "EXIT_STATUS: ${EXIT_STATUS}"

    if [ $EXIT_STATUS -ne 0 ]; then
        echo "Cannot access metrics. Killing app."
        kill -9 ${PID}
    fi

    #check thread count
    THREAD_COUNT1=`curl --basic -u admin:I_LXH,#2dddq -s http://127.0.0.1:${PORT}/metrics | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | grep "threads\":" | awk '{split($0,a,":"); print a[2]}' | xargs`
    EXIT_STATUS2=$?

    echo "Exit status from second call is: ${EXIT_STATUS2}"
    echo "Checking Thread count of ${PID} process: ${THREAD_COUNT1}"


    if [ $THREAD_COUNT1 -gt $MAX_THREAD_COUNT ] || [ $EXIT_STATUS2 -ne 0 ]; then
        echo "App has exceeded thread count. Killing app."
        kill -9 ${PID}
        break;
    else
        echo "Thread count ok"
    fi

    sleep 30 #try every 30 sec
done

echo "Ending watchdog"