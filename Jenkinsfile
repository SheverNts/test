node {
    scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
    git branch: "${env.BRANCH_NAME}", poll: false, url: "${scmUrl}"
    load "version.txt"

    shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    currentBuild.displayName = "#${BUILD_NUMBER} ${env.BRANCH_NAME}-${VERSION}.${shortCommit}"
    currentBuild.description = "Bundling the packages ..."
    withCredentials([usernamePassword(credentialsId: '22249e5d-7957-42df-98e3-03132d209161', passwordVariable: 'NexusPassword', usernameVariable: 'NexusUsername'), usernamePassword(credentialsId: '48ece0dc-4473-42b4-86c6-18214bda7f10', passwordVariable: 'TeamCityPassword', usernameVariable: 'TeamCityUsername')]) {
    stage('bundle') {
        sh "sh bundle.sh"
    }
    }
}