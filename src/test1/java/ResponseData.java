import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javabogLogin.ControllerBase;


public class ResponseData {
	private ControllerBase.LambdaResponse.LambdaResponseData data = new ControllerBase.LambdaResponse.LambdaResponseData();
	private ObjectNode body;
	private ObjectMapper mapper;
	
	public ResponseData(ByteArrayOutputStream buffer) throws JsonParseException, JsonMappingException, IOException{
		this(new ByteArrayInputStream(buffer.toByteArray()));
	}

	public ResponseData(ByteArrayInputStream buffer) throws JsonParseException, JsonMappingException, IOException {
		mapper = new ObjectMapper();
		data = mapper.readValue(buffer, data.getClass());
		if(data.body != null && !data.body.isEmpty())
			body = (ObjectNode)mapper.readTree(data.body);
	}
	
	public String getHeaderValue(String name){
		return data.headers.get(name);
	}
	
	public int getResponseCode(){
		return data.statusCode;
	}
	
	public String getBody(){
		return data.body;
	}
	
	public <T> T getBody(Class<T> type) throws JsonProcessingException{
		return mapper.treeToValue(body, type);
	}
	
	public <T> T getBody(String element, Class<T> type) throws JsonProcessingException{
		return mapper.treeToValue(body.get(element), type);
	}
}
