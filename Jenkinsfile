pipeline {
   agent any
//comment   
    environment {
       NAMESPACE = "mbbscf-be"
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
