pipeline {
   agent any
//comment   
    environment {
       NAMESPACE = "mbbscf-be"
       SNYK_HOME = tool 'snyk'
       SNYK_TOKEN = credentials('ait-snyk-token')
    }
  
   stages {
     stage('Check Commit') {
       steps {
        script {
          result = sh (script: "git log -1 | grep -E '(feat|build|chore|fix|docs|refactor|perf|style|test)(\\(.+\\))*:'", returnStatus: true)
          if (result != 0) {
            echo "warning, please follow commit standard"
          }
        }
       }
     }  

    stage('Snyk Code') {
      steps {
        script { 
          sh '${SNYK_HOME}/snyk-linux auth $SNYK_TOKEN'
          sh '${SNYK_HOME}/snyk-linux code test --json --severity-threshold=low --all-projects --target-reference=${BRANCH_NAME} | ${SNYK_HOME}/snyk-to-html-linux -o snyk.html'
          result = sh(script: "egrep '(0</strong> low issues|0</strong> medium issues|0</strong> high issues)' snyk.html | wc -l", returnStdout: true).trim()
          publishHTML (target : [allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: '.',
            reportFiles: 'snyk.html',
            reportName: 'Snyk Code Reports',
            reportTitles: 'Snyk Code Report'])
          if (result != '3') {
            echo " Vulnerabilities Found!!!! "
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE')            
            {  
                sh 'exit 1'
            }
          } else {
            echo " ===No Vulnerability Found=== "
          }
        }
      }
    }


    stage('Snyk Open Source') {
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          snykSecurity(
            snykInstallation: 'snyk',
            snykTokenId: 'snyk-token',
            additionalArguments: '--all-projects --target-reference=${BRANCH_NAME}'
          )
        }
      }
    }
      
      stage('Build Image - Push - Deploy') {
         when {
            branch "master"
              }
         steps {
            script {
               withCredentials([file(credentialsId: 'ait3-k8s', variable: 'AIT3K8SCONFIG'), 
                             usernamePassword(credentialsId: 'ait-k8s_docker-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                  sh 'docker login ait-cr.akarinti.tech --username=${USER} --password=${PASS}'
                  sh 'cat ${AIT3K8SCONFIG} > ~/.kube/ait3-k8s-config'
                  sh 'export KUBECONFIG=$HOME/.kube/config:$HOME/.kube/ait3-k8s-config && skaffold run -n ${NAMESPACE} --kube-context ait3-k8s'
               }
            }
         }
      }   
   }
  
   post {
    always {
      cleanWs()
         }
      }
   }
