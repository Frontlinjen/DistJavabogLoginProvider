

public class CognitoIdentityTest implements com.amazonaws.services.lambda.runtime.CognitoIdentity{
	String identityId;
	
	public void setIdentityId(String id){
		identityId = id;
	}

	public String getIdentityId() {
		return identityId;
	}

	public String getIdentityPoolId() {
		// TODO Auto-generated method stub
		return null;
	}

}
