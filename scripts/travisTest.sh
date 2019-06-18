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

postStatus="$(curl -X POST "http://localhost:31000/SessionsGuide/cart/eggs&2.29" --cookie "c.txt" --cookie-jar "c.txt")"
getStatus="$(curl --write-out "%{http_code}\n" --silent --output /dev/null "http://`minikube ip`:31000/SessionsGuide/cart" --cookie "c.txt" --cookie-jar "c.txt")"

echo post status 
echo "$postStatus"
echo get status
echo "$getStatus"
# if [ "$postStatus" == "eggs added to your cart and costs $2.29" ] && [ "$getStatus" == "200" ]
# then
#     echo POST/GET OK
# else
#     echo post status:
#     echo "$postStatus"
#     echo get status:
#     echo "$getStatus"
#     exit 1
# fi

kubectl logs $(kubectl get pods -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}' | grep cart)
