@ECHO OFF
IF "%1" == "" GOTO Done
java -jar tablab.jar --SquidLib %*
:Done
