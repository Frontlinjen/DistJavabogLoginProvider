import javabogLogin.InternalServerErrorException;
import javabogLogin.JavabogLogin;
import javabogLogin.LoginData;
import javabogLogin.RequestDataMock;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;


public class LoginTest {
	
	JavabogLogin login;
	LoginData loginData;
	ContextTest context;
	ObjectMapper mapper;
	ByteArrayInputStream in;
	ByteArrayOutputStream out;
	
	@Before
	public void setUp() throws Exception{
		login = new JavabogLogin();
		context = new ContextTest("Tester");
		mapper = new ObjectMapper();
		out = new ByteArrayOutputStream();
	}
	
	public LoginData testData(){
		loginData = new LoginData();
		return loginData;
		
	}
	
	@Test
	public void validLogin() throws IOException, InternalServerErrorException{
		LoginData loginData = testData();
		loginData.setUsername("s153448");
		loginData.setPassword("kode");
		RequestDataMock request = new RequestDataMock();
		request.setBody(mapper.writeValueAsString(loginData));
		
		login.handleRequest(new ByteArrayInputStream(request.getContent()), out, context);
		ResponseData response = new ResponseData(out);
		String token = response.getBody("Token", String.class);
		assertEquals(true, !token.isEmpty());
		
		
	}
	
	@Test
	public void incorrectCredentialsLogin() throws InternalServerErrorException, IOException{
		LoginData loginData = testData();
		loginData.setUsername("tester123");
		loginData.setPassword("kode");
		RequestDataMock request = new RequestDataMock();
		request.setBody(mapper.writeValueAsString(loginData));
		
		login.handleRequest(new ByteArrayInputStream(request.getContent()), out, context);
		ResponseData response = new ResponseData(out);
		assertEquals(response.getResponseCode(), 401);
	}
	
	@Test
	public void emptyUsernameAndPasswordLogin() throws InternalServerErrorException, IOException{
		LoginData loginData = testData();
		RequestDataMock request = new RequestDataMock();
		request.setBody(mapper.writeValueAsString(loginData));
		
		login.handleRequest(new ByteArrayInputStream(request.getContent()), out, context);
		ResponseData response = new ResponseData(out);
		assertEquals(response.getResponseCode(), 400);
		out.reset();
		
		loginData.setUsername("test123");
		login.handleRequest(new ByteArrayInputStream(request.getContent()), out, context);
		assertEquals(response.getResponseCode(), 400);
	}
	
}
