#!/bin/bash
set -x

TeamCityUrl=$TEAMCITY_URL
TeamCityUser=$TeamCityUsername
TeamCityPasswd=$TeamCityPassword
NexusUrl=$NEXUS_URL
NexusUser=$NexusUsername
NexusPasswd=$NexusPassword
TeamCityBuildID="Test_Build"

GetLatestBuildData () {
    local BuildType=$1
    local CPATH=$PATH
    export PATH="$PATH:./Utility"
    curl -u ${TeamCityUser}:${TeamCityPasswd} ${TeamCityUrl}/httpAuth/app/rest/builds/?locator=buildType:${BuildType},status:success,count:1  | xml2json -t xml2json -o metadata.json
    local id=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.id)
    local state=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.state)
    local status=$(cat metadata.json | sed  s/@//g | jq -r .builds.build.status)
   # rm -rf metadata.json metadata.xml
    export PATH=$CPATH
    printf "$id,$state,$status"
}

DownloadArtifact() {
    local BuildId=$1
    local ArtifactName=$2
    printf "\n Downloading Artifacts from Teamcity >> $TeamCityUrl:$TeamCityPort \n"
    curl -k -L -u ${TeamCityUser}:${TeamCityPasswd} ${TeamCityUrl}/repository/downloadAll/Test_Build/${BuildId}:id/artifacts.tgz -o $ArtifactName
}

UploadArtifact() {
    local artifact_name=$1
    printf "\n Upload Artifact to Nessus >> $NexusUrl \n"
    curl -k -L -v -u admin:admin123 --upload-file $artifact_name ${NexusUrl}
}

Versioning () {
    local artifact_name=$1
    source ./version.txt
    local shortCommit=$(git log -n 1 --pretty=format:'%h')
    cp $artifact_name artifacts.$VERSION.$shortCommit
    export FINALVERSION=artifacts.$VERSION.$shortCommit
}

DownloadArtifact $(GetLatestBuildData $TeamCityBuildID | awk -F "," '{print $1}') "artifacts.tgz"
Versioning artifacts.tgz 
UploadArtifact $FINALVERSION