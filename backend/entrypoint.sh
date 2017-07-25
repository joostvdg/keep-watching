#!/usr/bin/env bash
# postgres://cjtetdzimdkcyj:2c77dff5cbcc39feda255ad9bde803e4822b7a0ac20880cbf4c33e0a07963908@ec2-50-19-218-160.compute-1.amazonaws.com:5432/d1trtpsrrej15q
if [ "${DATABASE_URL}" ]; then
    URL=$DATABASE_URL
    INDEX_AT=`expr index "$URL" @`
    LENGTH_PREFIX=11
    LENGTH_AT=$[ INDEX_AT - LENGTH_PREFIX ]
    LENGTH_AT=$[LENGTH_AT -1 ]
    USR_PASS=${URL:11:$LENGTH_AT}
    INDEX_COLON=`expr index "$USR_PASS" :`
    USR=${USR_PASS:0:$INDEX_COLON-1}
    PSS=${USR_PASS:$INDEX_COLON}
    #echo "USR_PASS=$USR_PASS"
    #echo "USR=$USR"
    #echo "PSS=$PSS"
    URL_REMAINDER=${URL:$LENGTH_AT}
    #echo "URL_REMAINDER=$URL_REMAINDER"
    export JDBC_DATABASE_URL="jdbc:postgresql://$URL_REMAINDER"
    export JDBC_DATABASE_USERNAME="$USR"
    export JDBC_DATABASE_PASSWORD="$PSS"
fi
