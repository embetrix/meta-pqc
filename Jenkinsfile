pipeline {

    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'scarthgap', selectedValue: 'DEFAULT', name: 'BRANCH', type: 'PT_BRANCH', description: 'branch to build'
        choice choices: ['qemux86-64', 'beaglebone-yocto'], description: 'select machine', name: 'MACHINE'
        choice choices: ['pqc-demo-image'], description: 'select image', name: 'IMAGE'
        choice choices: ['no', 'yes'], description: 'clean workspace', name: 'CLEAN'
        choice choices: ['no', 'yes'], description: 'build sdk', name: 'SDK'
    }

    stages {
 
        stage('Clean') {
            when {
                expression { params.CLEAN == 'yes' }
            }
            steps {
               sh "git clean -fdx"
            }
        }

        stage('Build-Image') {
            steps {
                sh "KAS_MACHINE=${params.MACHINE} KAS_TARGET=${params.IMAGE} kas-container build --force-checkout --update kas-pqc.yml"
                archiveArtifacts artifacts: "build/tmp/deploy/images/${params.MACHINE}/${params.IMAGE}*.wic.* , \
                                             build/tmp/deploy/images/${params.MACHINE}/*.manifest",
                                             followSymlinks: true,
                                             fingerprint: true,
                                             onlyIfSuccessful: true
            }
        }

        stage ('Build-SDK') {
            when {
                expression { params.SDK == 'yes' }
            }
            steps {
               sh "KAS_MACHINE=${params.MACHINE} KAS_TARGET=${params.IMAGE} KAS_TASK=populate_sdk kas-container build kas-pqc.yml"
               archiveArtifacts artifacts: "build/tmp/deploy/sdk/*.sh" , onlyIfSuccessful: true
            }
        }

        stage ('Deploy-SDK') {
            when {
                expression { params.SDK == 'yes' }
            }
        }
    }
}
