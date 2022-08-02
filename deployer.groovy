pipeline
{
	agent { label 'master' }
	environment
	{
		DEV_CLONE_URL = "git@github.com:hyrglobalsource/optjobs.git"
		DEV_DIR = "$WORKSPACE" + "/devCode"
		DEVOPS_DIR = "$WORKSPACE" + "/devopsCode"
		SKIP_TLS = true
		DEPLOY_ENV = "staging"
		DEV_BRANCH = ""


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
					stage('CHECKOUT DEV CODE')
					{
						dir(DEV_DIR)
						{
							checkout scm
							//checkout([$class: 'GitSCM', branches: [[name: DEV_BRANCH]], extensions: [], userRemoteConfigs: [[url: DEV_CLONE_URL]]])
						}

					}
				}
			}
		}
	}
}