## Kubernetes deployment

The project includes a script to build a docker image including all job dependencies and upload that image to a locally installed **Minikube** instance.

Once the image is uploaded it will try to create one job cluster manager and **2** task-managers, this can be configured by modifying the build.sh file and changing arguments named
`FLINK_JOB_PARALLELISM={numberOfSlaves}`.

The flink version binaries are not included ,  to speed up deployment speed,the script will download the flink binaries from
https://flink.apache.org/downloads.html (flink-1.10.0-bin-scala_2.12.tgz) , and place them in `/container` folder.

The job binaries are hardcoded as **flink-job-1.0-SNAPSHOT.jar** this will need to be improved in the future.

The build.sh script needs to be run in a unix/linux style shell as it uses `envsubst` not included in windows command line interface. (tested with **GitBash**)

The following files will be generated if further modifications are needed:

```
job-cluster-job.yaml
job-cluster-service.yaml
task-manager-deployment.yaml
```

Metrics are not working in this branch , a way to configure the execution environment to specify metrics config seems to be missing

To run the script run:

```
sh build.sh
```