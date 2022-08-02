pipeline
{
	agent { label 'master' }
	environment
	{
		// variables start 
		DEV_CLONE_URL = "https://github.com/hyrglobalsource/optjobs.git"
		DEPLOY_ENV = "staging"
		DEV_BRANCH = "secrets-keys-integration"
		APP_NAME = "optjobs-backend"
		// variables end
		DEV_DIR = "$WORKSPACE" + "/devCode"
		DEVOPS_DIR = "$WORKSPACE" + "/devopsCode"
		SKIP_TLS = true
		DOCKER_IMAGE_PREFIX = "ec2-18-144-27-149.us-west-1.compute.amazonaws.com/optjobs/"
		DOCKER_FILE_PATH = "$DEVOPS_DIR"+"/docker-files/staging/"+"$APP_NAME"+"/Dockerfile"


	}
	options
	{
		timeout(time:2, unit:'HOURS')
		timestamps()
	}
	stages
	{
		stage('INITIATE')
		{
			steps
			{
				script
				{
					stage('CHECKOUT DEVOPS CODE')
					{
						dir(DEVOPS_DIR)
						{
							checkout scm
						}

					}
					stage('CHECKOUT DEV CODE')
					{
						dir(DEV_DIR)
						{
							checkout([$class: 'GitSCM', branches: [[name: DEV_BRANCH]], extensions: [], userRemoteConfigs: [[credentialsId: 'github-user-token', url: DEV_CLONE_URL]]])
							sh "cp $DOCKER_FILE_PATH ."
						}

					}
				}
			}
		}
		stage('DOCKER BUILD')
		{
			steps
			{
				script
				{
					stage('IMAGE BUILD')
					{
						dir(DEV_DIR)
						{
							registry = "$DOCKER_IMAGE_PREFIX"+"$APP_NAME"+":"+"$BUILD_NUMBER"
							docker.build registry

						}

					}
				}
			}
			

		}
	}
}