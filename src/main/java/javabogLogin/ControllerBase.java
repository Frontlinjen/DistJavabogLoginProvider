package javabogLogin;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ControllerBase {
	static public class LambdaResponse{
		static public class LambdaResponseData{
			public LambdaResponseData(){
				//Must always include this header for client requests to work....
				headers.put("Access-Control-Allow-Origin", "*");
			}
			
			@JsonProperty("isBase64Encoded")
			public boolean base64 = false;
			public int statusCode;
			public Map<String, String> headers = new HashMap<String, String>();
			public String body;
		}
		private LambdaResponseData data;
		private JsonGenerator bodyWriter;
		private StringWriter bodyStream;
				
		public LambdaResponse(JsonFactory factory){
			data = new LambdaResponseData();
			data.statusCode = 200;
			try {
				bodyStream = new StringWriter();
				bodyWriter = factory.createGenerator(bodyStream);
				bodyWriter.writeStartObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void addHeader(String header, String value){
			data.headers.put(header, value);
		}
		
		public void setStatusCode(int statusCode){
			data.statusCode = statusCode;
		}

		public void addResponseObject (String ID, Object object) {
			try {
				bodyWriter.writeObjectField(ID, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void dispatch(JsonGenerator out){
			try {
				bodyWriter.writeEndObject();
				bodyWriter.flush();
				data.body = bodyStream.toString();
				out.writeObject(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
				
	}
	static public class LambdaRequest{
		@JsonIgnoreProperties(ignoreUnknown=true)
		static public class LambdaRequestData{
			public Map<String, String> headers = new HashMap<String, String>();
			@JsonProperty("queryStringParameters")
			public Map<String, String> queryString = new HashMap<String, String>();
			public Map<String, String> pathParameters = new HashMap<String, String>();
			void setBody(String content) throws JsonProcessingException, IOException{
				ObjectMapper mapper = new ObjectMapper();
				if(content != null && !content.isEmpty())
					body = (ObjectNode)mapper.readTree(content);
			}
			public ObjectNode body;
		}
		
		private ObjectCodec objectConverter;
		private LambdaRequestData data;
		public LambdaRequest(JsonParser parser, ObjectCodec c)
		{
			try {
				data = parser.readValueAs(LambdaRequestData.class);
				objectConverter = c;
			} catch (IOException e) {
				//TODO: Handle gracefully
				e.printStackTrace();
			}
		}
		
		public String getHeader(String name){
			return data.headers.get(name);
		}
		
		public String getQuery(String id){
			return data.queryString.get(id);
		}
		
		public String getPath(String id){
			return data.pathParameters.get(id);
		}
		
		public <T> T getObject(String name, Class<T> type){
			if(data.body != null && data.body.has(name)){
				try {
					TreeNode node = data.body.get(name);
					if(node != null)
						return objectConverter.treeToValue(node, type);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		public <T> T getObject(Class<T> type){
			if(data.body != null)
			{
				try {
					return objectConverter.treeToValue(data.body, type);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	private ObjectMapper mapper = new ObjectMapper();
	private JsonFactory factory = new JsonFactory(mapper);

	protected LambdaResponse response;
	protected LambdaRequest request;
	
	
	private class Error{
		public Error(String s){
			error = s;
		}
		public String error;
	}
	protected void raiseError(OutputStream out, int errorCode, String value) throws InternalServerErrorException{
		//Discard current procress
		request = null;
		response = null;
		try {
			//Write emergency data
			LambdaResponse.LambdaResponseData data = new LambdaResponse.LambdaResponseData();
			data.statusCode = errorCode;
			data.body = mapper.writeValueAsString(new Error(value));
			JsonGenerator gen = factory.createGenerator(out);
			//gen.writeStartObject();
			gen.writeObject(data);
			//gen.writeEndObject();
			gen.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new InternalServerErrorException("Failed to write response");
		}
		
		
	}
	
	protected  boolean verifyLogin(Context context)
	{
		return !(context.getIdentity() == null || context.getIdentity().getIdentityId().isEmpty());
	}
	
	
	protected void FinishRequest(OutputStream o) throws InternalServerErrorException
	{
		if(response != null)
		{
			try {
				response.dispatch(factory.createGenerator(o));
			} catch (IOException e) {
				throw new InternalServerErrorException("Failed to write response");
			}
		}
	}
	
	protected void StartRequest(InputStream i)
	{
		response = new LambdaResponse(factory);
		try {
			request = new LambdaRequest(factory.createParser(i), new ObjectMapper());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
