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
		DEV_BRANCH = "main"


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
							//checkout([$class: 'GitSCM', branches: [[name: DEV_BRANCH]], extensions: [], userRemoteConfigs: [[url: DEV_CLONE_URL]]])
						}

					}
					stage('CHECKOUT DEV CODE')
					{
						dir(DEVOPS_DIR)
						{
							checkout([$class: 'GitSCM', branches: [[name: DEV_BRANCH]], extensions: [], userRemoteConfigs: [[url: DEV_CLONE_URL]]])
						}

					}
				}
			}
		}
	}
}