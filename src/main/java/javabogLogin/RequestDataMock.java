package javabogLogin;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RequestDataMock {
	private ObjectMapper mapper = new ObjectMapper();
	private RequestData data = new RequestData();
	static public class RequestData {
		public Map<String, String> headers = new HashMap<String, String>();
		@JsonProperty("queryStringParameters")
		public Map<String, String> queryString = new HashMap<String, String>();
		public Map<String, String> pathParameters = new HashMap<String, String>();			
		public String body;
	}
	
	
	public void setBody(String content){
		data.body = content;
	}
	
	public void addHeader(String name, String value){
		data.headers.put(name, value);
	}
	
	public void addQuery(String name, String value){
		data.queryString.put(name, value);
	}
	
	public void addPath(String name, String value){
		data.pathParameters.put(name, value);
	}
	
	public byte[] getContent() throws JsonProcessingException{
		return mapper.writeValueAsBytes(data);
	}
	
}
 