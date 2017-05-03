package javabogLogin;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;

import javax.xml.ws.WebServiceException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.*;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.soap.Brugeradmin;


import java.io.InputStream;
import java.io.OutputStream;


@SuppressWarnings("restriction")
public class JavabogLogin extends ControllerBase{
	final static String PoolID = "eu-west-1:b407e22a-76a1-41bc-87f2-0d9278f62fb4";
	final static String domain = "login.javabog.dk";
	
	public static AWSCredentials GetCredentials() {
		return new BasicAWSCredentials("AKIAIFGV6XJH5XTI6L6Q", "4Bc6BWvBkmC/OGyoTBwshTUo9rhV19ow7eUbhPP9");
		
	}
	@SuppressWarnings("deprecation")
	public void handleRequest(InputStream in, OutputStream out, Context context) throws InternalServerErrorException{
		
		try {
			StartRequest(in);
			LoginData login = request.getObject(LoginData.class);
			if(login.username == null){
				raiseError(out, 400, "Username not entered");
				return;
			}
			if(login.password == null){
				raiseError(out, 400, "Password not entered");
			}
			java.net.URL url;
			url = new java.net.URL("http://javabog.dk:9901/brugeradmin?wsdl"); //UnkownHostException
			QName qname = new QName("http://soap.transport.brugerautorisation/", "BrugeradminImplService");
			Service service = null;
			try{
				service = Service.create(url, qname);
			}
			catch(WebServiceException e){
				raiseError(out, 500, "Service unavailable");
				return;
			}
			Brugeradmin ba = service.getPort(Brugeradmin.class);
			try{
				Bruger b = ba.hentBruger(login.username, login.password);
			}catch(Exception e){
				raiseError(out, 401, "Wrong username or password");
				return;
			}
			AmazonCognitoIdentity client = new AmazonCognitoIdentityClient(GetCredentials());
			client.setRegion(Region.getRegion(Regions.EU_WEST_1));
			
			GetOpenIdTokenForDeveloperIdentityRequest requestData = new GetOpenIdTokenForDeveloperIdentityRequest();
			requestData.setIdentityPoolId(PoolID);
			requestData.addLoginsEntry(domain, login.username);
			
			
			GetOpenIdTokenForDeveloperIdentityResult result = client.getOpenIdTokenForDeveloperIdentity(requestData);
			response.addResponseObject("Token", result.getToken());//System.out.println("Got ID: " + result.getIdentityId() + " and token: " + result.getToken());
			FinishRequest(out);
		} catch (Exception e) {
			if(e instanceof UnknownHostException){
				
			}
			raiseError(out, 500, "(╯°□°）╯︵ ┻━┻"); 		
			return;
		}
	}
}
