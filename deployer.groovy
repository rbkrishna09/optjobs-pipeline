pipeline
{
	environment
	{
		DEV_CLONE_URL = ""
		DEV_DIR = "$WORKSPACE" + "/devCode"
		DEVOPS_DIR = "$WORKSPACE" + "/devopsCode"
		SKIP_TLS = true
		DEPLOY_ENV = "staging"


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


					}
				}
			}
		}
	}
}