package common;

public interface Validable
{

	public ErrorMessage[] validate(
	    boolean selective,
	    Translator t);

}
