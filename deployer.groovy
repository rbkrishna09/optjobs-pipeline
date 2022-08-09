pipeline
{
	agent { label 'master' }
	environment
	{
		// variables start 
		// DEV_CLONE_URL = "https://github.com/hyrglobalsource/optjobs-ui.git"
		// DEPLOY_ENV = "staging"
		// DEV_BRANCH = "stage"
		// APP_NAME = "optjobs-frontend"
		// variables end

		DEV_DIR = "$WORKSPACE" + "/devCode"
		DEVOPS_DIR = "$WORKSPACE" + "/devopsCode"
		SKIP_TLS = true
		DOCKER_REGISTRY_PATH = "http://ec2-18-144-27-149.us-west-1.compute.amazonaws.com"
		DOCKER_IMAGE_PREFIX = "ec2-18-144-27-149.us-west-1.compute.amazonaws.com/optjobs/"
		DOCKER_FILE_PATH = "$DEVOPS_DIR"+"/docker-files/${DEPLOY_ENV}/"+"$APP_NAME"+"/Dockerfile"
		DOCKER_REGISTRY = "$DOCKER_IMAGE_PREFIX"+"$APP_NAME"+":"+"latest-"+"$DEPLOY_ENV"
		def DEV_CLONE_URL = ""
		BUILT_DOCKER_IMAGE = ''
		def APP_PORT = "3001:3001"



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
					stage('Setup Params')
					{
						if(APP_NAME == "optjobs_frontend")
						{
							DEV_CLONE_URL = "https://github.com/hyrglobalsource/optjobs-ui.git"
						}
						else if (APP_NAME == "optjobs_backend")
						{
							DEV_CLONE_URL = "https://github.com/hyrglobalsource/optjobs.git"
						}

					}
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
		stage('DOCKER PROCESSING')
		{
			steps
			{
				script
				{
					stage('IMAGE BUILD')
					{
						dir(DEV_DIR)
						{
							
							BUILT_DOCKER_IMAGE = docker.build DOCKER_REGISTRY

						}

					}
					stage('IMAGE PUSH')
					{
						dir(DEV_DIR)
						{
							docker.withRegistry(DOCKER_REGISTRY_PATH,'harbor_creds')
							{
								BUILT_DOCKER_IMAGE.push()


							}
						}
					}
					stage('Deploy application')
					{
						ansiblePlaybook become: true, credentialsId: 'ubuntu-private', disableHostKeyChecking: true, extras: "-e \"host=$APP_NAME docker_image=$DOCKER_REGISTRY app_name=$APP_NAME app_ports=$APP_PORT\"", installation: 'ansible-new', inventory: 'inventory', playbook: 'deploy.yaml'
					}
				}
			}
			

		}
	}
}