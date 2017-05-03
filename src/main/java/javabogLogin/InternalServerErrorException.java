package javabogLogin;


public class InternalServerErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String s){
		super("InternalServerError " + s);
		}
}
