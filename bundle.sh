#!/bin/bash
TeamCityUrl=
TeamCityPort=
TeamCityUser=
TeamCityPasswd=
NexusUrl=
NexusPort=
NexusUser=
NexusPasswd=

GetLatestBuildData () {
    local BuildType=$1
    local CPATH=$PATH
    export PATH="$PATH:./Utility"
    curl -u ${TeamCityUser}:${TeamCityPasswd} \
        http://${$TeamCityUrl}:${TeamCityPort}/httpAuth/app/rest/builds/?locator=buildType:${BuildType},status:success,count:1 | xml2json \
        -t xml2json -o metadata.json
    local id=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.id)
    local state=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.state)
    local status=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.status)
    rm -rf metadata.json
    export PATH=$CPATH
    printf "$id,$state,$status"
}

DownloadArtifact() {
    local BuildId=$1
    local ArtifactName=$2
    printf "\n Downloading Artifacts from Teamcity >> $TeamCityUrl:$TeamCityPort \n"
    curl -k -L -u ${TeamCityUser}:${TeamCityPasswd} \
        http://${$TeamCityUrl}:${TeamCityPort}/repository/downloadAll/Test_Build/${BuildId}:id/artifacts.tgz -o ArtifactName
}

UploadArtifact() {
    local artifact_name=$1
    printf "\n Upload Artifact to Nessus >> $NexusUrl \n"
    curl -k -L -v -u admin:admin123 --upload-file $artifact_name \
        http://${NexusUrl}:${NexusPort}/nexus/content/repositories/releases/org/
}

Versioning () {
    local artifact_name=$1
    source version.txt
    cp $artifact_name artifacts.$VERSION.$shortCommit
    export FINALVERSION=artifacts.$VERSION.$shortCommit
}

DownloadArtifact $(GetLatestBuildData| awk -F "," '{print $1}') artifacts.tgz
Versioning artifacts.tgz 
UploadArtifact $FINALVERSION