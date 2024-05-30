#!/bin/bash

#写一个文件夹循环cd
for dir in ./implementations/*; do
    cd "$dir" || exit
    #执行编译命令
    mvn paper-nms:init -f pom.xml
    #编译完成后，返回上一级目录
    cd ..
done

mvn clean package