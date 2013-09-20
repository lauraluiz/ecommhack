#! /bin/bash

curl -s https://sandbox.pactas.com/signup-iframe/5231dfec1d8dd00f389dc547?language=en >current-iframe-content
number_of_differences=$(diff current-iframe-content expected-iframe-content | wc -l | tr -d ' ')
if [ "${number_of_differences}" != "10" ]; then
    echo "There are changes!!!"
    diff current-iframe-content expected-iframe-content 
    exit 1
fi
