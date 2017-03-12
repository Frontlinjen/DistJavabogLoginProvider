package javabogLogin;
import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.WebIdentityFederationSessionCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.*;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.soap.Brugeradmin;


public class JavabogLogin implements RequestHandler<LoginData, String> {
	final static String PoolID = "eu-west-1:b407e22a-76a1-41bc-87f2-0d9278f62fb4";
	final static String domain = "login.javabog.dk";
	
	public static AWSCredentials GetCredentials() {
		return new BasicAWSCredentials("AKIAIFGV6XJH5XTI6L6Q", "4Bc6BWvBkmC/OGyoTBwshTUo9rhV19ow7eUbhPP9");
		
	}
	public String handleRequest(LoginData login, Context context){
		
		java.net.URL url;
		try {
			url = new java.net.URL("http://javabog.dk:9901/brugeradmin?wsdl");
			QName qname = new QName("http://soap.transport.brugerautorisation/", "BrugeradminImplService");
			Service service = Service.create(url, qname);
			Brugeradmin ba = service.getPort(Brugeradmin.class);
			Bruger b = ba.hentBruger(login.username, login.password);
			AmazonCognitoIdentity client = new AmazonCognitoIdentityClient();
			client.setRegion(Region.getRegion(Regions.EU_WEST_1));
			
			GetOpenIdTokenForDeveloperIdentityRequest requestData = new GetOpenIdTokenForDeveloperIdentityRequest();
			requestData.setIdentityPoolId(PoolID);
			requestData.addLoginsEntry(domain, login.username);
			
			
			GetOpenIdTokenForDeveloperIdentityResult result = client.getOpenIdTokenForDeveloperIdentity(requestData);
			return result.getToken();//System.out.println("Got ID: " + result.getIdentityId() + " and token: " + result.getToken());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
