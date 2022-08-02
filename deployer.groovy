pipeline
{
	agent { label 'master' }
	environment
	{
		DEV_CLONE_URL = "https://github.com/hyrglobalsource/optjobs.git"
		DEV_DIR = "$WORKSPACE" + "/devCode"
		DEVOPS_DIR = "$WORKSPACE" + "/devopsCode"
		SKIP_TLS = true
		DEPLOY_ENV = "staging"
		DEV_BRANCH = "secrets-keys-integration"


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
							checkout([$class: 'GitSCM', branches: [[name: DEV_BRANCH]], extensions: [], userRemoteConfigs: [[url: DEV_CLONE_URL]]])
						}

					}
				}
			}
		}
	}
}