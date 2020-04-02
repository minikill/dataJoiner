#!/bin/bash

mvn clean package

eval $(minikube docker-env)

docker build ./ -t data_joiner

kubectl create -f ./kubernetes/configMaps.yml
kubectl create -f ./kubernetes/appDeploy.yml
kubectl create -f ./kubernetes/appService.yml

helm init
helm upgrade --install  --wait -f ./kubernetes/prometheus-operator/values.yaml prometheus-operator stable/prometheus-operator
helm upgrade --install  --wait -f ./kubernetes/prometheus-adapter/values.yaml prometheus-adapter stable/prometheus-adapter

kubectl create -f ./kubernetes/appServiceMonitor.yml

kubectl create -f ./kubernetes/appHpa.yml
