

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ContextTest implements Context{
	CognitoIdentityTest cognito = new CognitoIdentityTest();
	
	public ContextTest(String identity){
		cognito.setIdentityId(identity);
	}
	
	public void setIdentity(String s)
	{
		if(cognito == null)
		{
			cognito = new CognitoIdentityTest();
		}
		cognito.setIdentityId(s);
	}
	
	public void clearIdentity()
	{
		cognito = null;
	}
	
	
	public String getAwsRequestId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLogGroupName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLogStreamName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFunctionName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFunctionVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInvokedFunctionArn() {
		// TODO Auto-generated method stub
		return null;
	}

	public CognitoIdentity getIdentity() {
		return cognito;
	}

	public ClientContext getClientContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRemainingTimeInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMemoryLimitInMB() {
		// TODO Auto-generated method stub
		return 0;
	}

	public LambdaLogger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
