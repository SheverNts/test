properties([ 
parameters([
[$class: 'ChoiceParameter', 
choiceType: 'PT_SINGLE_SELECT', 
description: 'Select a choice', 
filterable: true, 
name: 'ENV', 
randomName: 'choice-parameter-7601235200970', 
script: [$class: 'GroovyScript', 
fallbackScript: [classpath: [], 
                sandbox: true, 
                script: 'return ["ERROR"]'], 
                script: [classpath: [], 
                         sandbox: true,
                        script: "return['Dev','Stag','Prod','Test']"]
                ]
        ], 

[$class: 'ChoiceParameter', 
choiceType: 'PT_SINGLE_SELECT', 
description: 'Select a choice', 
filterable: true, 
name: 'SERVICE', 
randomName: 'choice-parameter-7601235200970', 
script: [$class: 'GroovyScript', 
fallbackScript: [classpath: [], 
                sandbox: true, 
                script: 'return ["ERROR"]'], 
                script: [classpath: [], 
                         sandbox: true,
                        script: "return${env.repo_list}"]
                ]
        ],


[$class: 'CascadeChoiceParameter', 
choiceType: 'PT_SINGLE_SELECT', 
description: 'Active Choices Reactive parameter',
filterable: true, 
name: 'Server', 
randomName: 'choice-parameter-7601237141171', 
referencedParameters: 'ENV', 
script: [$class: 'GroovyScript', 
fallbackScript: [classpath: [], 
                 sandbox: true,
                 script: 'return ["error"]'],
                script: [classpath: [], 
                sandbox: true, 
                script: 'if(ENV.equals("Dev")) {return [\'DEV1\', \'DEV2\', \'Dev3\', \'Dev4\']} else if(ENV.equals("Stag")) {return [\'Stag1\',\'Stag2\',\'Stag3\',\'Stag4\']} else {return [\'Prod1\',\'Prod2\']}']]
        ],

string(defaultValue: '', description: '', name: 'Artifact_Name', trim: true)
])
])

pipeline {
    agent any
    environment {
    ARTIFACT_NAME="${env.JOB_NAME}-${env.BUILD_NUMBER}"
    instance = sh (returnStdout: true, script: "`cat env.json |jq -r '.$Server.instance'`")
    }
    tools { 
        maven 'maven-3.6.1' 
        jdk 'jdk8' 
        }
    stages {
        stage ('SCM') {
            steps {
                script {
                  git branch: 'maven-tomcat', url: 'https://gitlab.com/beer786/newproject.git';
                }
                  }
                }

        stage('Build') {
           steps {    
                //sh 'mvn clean package -DskipTests=true'     
                sh 'echo hei'
                }      
              }

        stage('test') {
         steps {
            //sh 'mvn test'
            sh 'echo helo'
               }
             }
 
        stage('Archive') {
            steps {
             // archiveArtifacts 'target/*zip'
             sh 'echo hleo'
                } 
             }  
        stage('Environment') {
            steps {
               sh '''
                export instance = `cat env.json | jq -r .$Server.instance`
                export path = `cat env.json | jq -r .$Server.instancePath`
                '''
            }
        }
        stage('publish_nexus') {
            steps {
                script {
                    pom = readMavenPom  file: "pom.xml"
                    pom2 = readMavenPom file:  "src/assembly/testpom.xml"
                    ArtId = pom.artifactId
                    GrpId = pom.groupId
                    Pkg = pom.packaging
                    zipid = pom2.groupId
                    echo "pom2 file ${zipid}"
                    nexusArtifactUploader artifacts: [[artifactId: ArtId, classifier: '', file: "target/maven-simple-${ARTIFACT_NAME}-SNAPSHOT.jar", type: Pkg ]], 
                    credentialsId: 'nexus-creds', 
                    groupId: GrpId, 
                    nexusUrl: 'localhost:8081', 
                    nexusVersion: 'nexus3',
                    protocol: 'http', 
                    repository: 'test-maven', 
                    version: '0.2.2'           
            }
            }
        }
        stage('Downstream') {
            steps {
                sh 'echo ${env.path}'
                build job: 'test-parameter', 
                parameters: [string(name: 'ARTIFACT_NAME', value: "${ARTIFACT_NAME}")]
                }
            }
    }
    
    // post {
    //     always {
    //         junit 'target/surefire-reports/*.xml'
    //     }
    //}
}
