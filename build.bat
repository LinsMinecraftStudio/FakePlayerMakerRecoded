@echo off

echo Start building...

for /d %%i in (*) do (
    cd %%i
    mvn paper-nms:init -f pom.xml
    cd ..
)

mvn clean package