#!groovy
@Library('amf-jenkins-library') _

import groovy.transform.Field

def SLACK_CHANNEL = '#amf-jenkins'
def PRODUCT_NAME = "AMF"
def lastStage = ""
def color = '#FF8C00'
def headerFlavour = "WARNING"
@Field AMF_AML_JOB = "application/AMF/amf-aml/develop"

pipeline {
  options {
    timeout(time: 30, unit: 'MINUTES')
    ansiColor('xterm')
  }
  agent {
    dockerfile {
      filename 'Dockerfile'
      registryCredentialsId 'dockerhub-pro-credentials'
    }
  }
  environment {
    NEXUS = credentials('exchange-nexus')
    GITHUB_ORG = 'aml-org'
    GITHUB_REPO = 'amf-core'
  }
  stages {
    stage('Test') {
      steps {
        script {
          lastStage = env.STAGE_NAME
          sh 'sbt -mem 4096 -Dfile.encoding=UTF-8 clean coverage test coverageAggregate'
        }
      }
    }
    stage('Coverage') {
      when {
        anyOf {
            branch 'develop'
            branch 'fix-sonar-integration'
        }
      }
      steps {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL']]) {
            script {
                lastStage = env.STAGE_NAME
                sh 'sbt -Dsonar.host.url=${SONAR_SERVER_URL} sonarScan'
            }
        }
      }
    }
    stage('Publish') {
      when {
        anyOf {
          branch 'master'
          branch 'develop'
          branch 'release/*'
        }
      }
      steps {
        script {
            lastStage = env.STAGE_NAME
            sh 'sbt publish'
        }
      }
    }
    stage('Triggers') {
      when {
        anyOf {
          branch 'develop'
        }
      }
      steps {
        script {
          lastStage = env.STAGE_NAME
          echo "Triggering amf-aml on develop branch"
          build job: AMF_AML_JOB, wait: false
        }
      }
    }
    stage('Tag version') {
      when {
        anyOf {
          branch 'master'
          branch 'support/*'
        }
      }
      steps {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-salt', passwordVariable: 'GITHUB_PASS', usernameVariable: 'GITHUB_USER']]) {
          script {
            lastStage = env.STAGE_NAME
            def version = sbtArtifactVersion("coreJVM")
            tagCommitToGithub(version)
          }
        }
      }
    }
  }
  post {
      unsuccessful {
        failureSlackNotify(lastStage, SLACK_CHANNEL, PRODUCT_NAME)
      }
      success {
        successSlackNotify(SLACK_CHANNEL, PRODUCT_NAME)
    }
  }
}
