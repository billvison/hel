#!/bin/sh
echo "start at: " `date +%Y%m%d%k%M%S`

## service name
APP_NAME=${project.artifactId}
SERVICE_NAME=${project.build.finalName}
JAR_NAME=$SERVICE_NAME\.jar
PID=$SERVICE_NAME\.pid

program_dir=`dirname $0`
if [ `echo "$0" |grep -c "/"` -le 0 ];then
   program_dir=`pwd`
else
   program_dir=`cd ${program_dir};pwd`
fi

cd ${program_dir}
#cd ..
echo `pwd`
# sh clean.sh
echo "start process at:" `pwd`
mkdir logs

case "$1" in
    start)
        nohup java -Xms256M -Xmx1024M -Xss512k -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -Xloggc:logs/gc.log -Dloader.path=data/,lib/,conf/,front-end/ -Dspring.profiles.active=dev -jar $JAR_NAME &

        echo $! > $program_dir/$PID
        echo "=== start $SERVICE_NAME"
        ;;

    stop)
        kill `cat $program_dir/$PID`
        rm -rf $program_dir/$PID
        echo "=== stop $SERVICE_NAME"

        sleep 5
		##
        P_ID=`ps -ef | grep -w "$SERVICE_NAME" | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "=== $SERVICE_NAME process not exists or stop success"
        else
            echo "=== $SERVICE_NAME process pid is:$P_ID"
            echo "=== begin kill $SERVICE_NAME process, pid is:$P_ID"
            kill -9 $P_ID
        fi
        ;;

    restart)
        $0 stop
        sleep 2
        $0 start
        echo "=== restart $SERVICE_NAME"
        ;;

    *)
        ## restart
        $0 stop
        sleep 2
        $0 start
        ;;

esac

echo "start ."
exit 0