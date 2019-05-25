// vars/defaultUtilityPipeline.groovy
// This pipeline requires no parameters as input.
 
def call(Map pipelineParams) {
    pipeline {
        agent none
 
        stages {
            stage('Build and Unit test') {
                agent { label 'maven' }
                steps {
                    script {
                        module_Maven('clean verify')
                    }
                }
                post {
                    always {
                        junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: false
                    }
                    changed {
                        module_Notification.sendEmail(currentBuild.result)
                    }
                }
            }
            stage('Publish to Nexus') {
                agent { label 'maven' }
                steps {
                    script {
                        echo "This is where we publish to Nexus"
                        module_Artifact.publish()
                    }
                }
            }
        }
        post {
            always {
                script {
                    module_Notification.sendEmail(currentBuild.result)
                }
            }
        }
    }
}