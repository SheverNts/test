node {
    scmUrl = scm.getUserRemoteConfigs()[0].getUrl()
    git branch: "${env.BRANCH_NAME}", poll: false, url: "${scmUrl}"
    load "version.txt"
    shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
    currentBuild.displayName = "#${BUILD_NUMBER} ${env.BRANCH_NAME}-${VERSION}.${shortCommit}"
    currentBuild.description = "fooDescription "
    stage('build') {
        sh "sh bundle.sh"
        sh "ls -la"
        sh "env"
    }

}