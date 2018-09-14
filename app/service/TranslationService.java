package service;

import java.util.Locale;
import common.Translator;

public interface TranslationService
{

	// --- STATIC FIELDS --- //

	public static final Translator DUMMY = new Translator()
	{
		public String get(
		    String key,
		    Object... params)
		{
			return key;
		}

		@Override
		public Locale getLocale()
		{
			return Locale.ENGLISH;
		}
	};

	// --- FIELDS --- //

	public Translator forLocale(
	    Locale l);

}
