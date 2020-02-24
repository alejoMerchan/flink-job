#!/usr/bin/env bash
set -x

mvn clean package

eval "$(minikube docker-env)"

sh ./container/docker/build.sh --from-archive ./container/flink-1.9.2-bin-scala_2.12.tgz --hadoop-version 2.8 --job-artifacts ./target/flink-job-1.0-SNAPSHOT.jar --with-python3 --image-name flinkjob

cp ./container/kubernetes/job-cluster-service.yaml job-cluster-service.yaml .
FLINK_IMAGE_NAME=flinkjob FLINK_JOB_PARALLELISM=2 envsubst < ./container/kubernetes/task-manager-deployment.yaml.template > "task-manager-deployment.yaml"
FLINK_IMAGE_NAME=flinkjob FLINK_JOB_PARALLELISM=2 envsubst < ./container/kubernetes/job-cluster-job.yaml.template > "job-cluster-job.yaml"

kubectl delete -f ./
kubectl apply -f ./container/