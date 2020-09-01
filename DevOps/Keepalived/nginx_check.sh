#!/bin/bash
A=$(ps -C nginx --no-header | wc -l)
if [ $A -eq 0 ]; then
    systemctl start nginx
    sleep 2
    if [ $(ps -C nginx --no-header | wc -l) -eq 0 ]; then
        systemctl stop keepalived
    fi
fi