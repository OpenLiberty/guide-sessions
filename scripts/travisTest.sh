#!/bin/bash
set -euxo pipefail

##############################################################################
##
##  Travis CI test script
##
##############################################################################

mvn -q package

docker build -t cart-app:1.0-SNAPSHOT .

sleep 120

kubectl apply -f kubernetes.yaml

sleep 120

kubectl get pods

echo `minikube ip`

cartStatus="$(curl --write-out "%{http_code}\n" --silent --output /dev/null "http://`minikube ip`:31000/openapi/ui/")"

if [ "$cartStatus" == "200" ] 
then
    echo ENDPOINT OK
else
    echo cart status:
    echo "$cartStatus"
    exit 1
fi

mvn verify -Ddockerfile.skip=true -Dcluster.ip=`minikube ip`

kubectl logs $(kubectl get pods -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}' | grep cart)
