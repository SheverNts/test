node {
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



try {
        stage ('SCM') {
                script {
                  git branch: 'maven-tomcat', url: 'https://gitlab.com/beer786/newproject.git';
                }
                  }


        stage('Build') {
                //sh 'mvn clean package -DskipTests=true'     
                sh 'echo hei'
                }


stage('test') {
            //sh 'mvn test'
            sh 'echo helo'
               }
             
 
        stage('Archive') {

             // archiveArtifacts 'target/*zip'
             sh 'echo hleo'
                } 
             
        stage('Environment') {
              sh 'echo ecn'
               //sh '''
                //export instance = `cat env.json | jq -r .$Server.instance`
                //export path = `cat env.json | jq -r .$Server.instancePath`
                //'''
            }

} finally {

stage('junit')  {
             junit 'target/surefire-reports/*.xml'
        }

}

}

