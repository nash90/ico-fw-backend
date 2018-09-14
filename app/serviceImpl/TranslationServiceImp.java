package serviceImpl;

import javax.inject.Inject;
import common.ServerException;
import play.Environment;

public class TranslationServiceImp
    extends
        JsonTranslationService
{

	private static final String FILE_PATH = "dictionary.json";

	@Inject
	public TranslationServiceImp(
	                             final Environment env)
	    throws ServerException
	{
		super.load(env.resourceAsStream(FILE_PATH));
	}

}
