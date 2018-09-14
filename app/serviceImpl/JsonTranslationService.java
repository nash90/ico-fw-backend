package serviceImpl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import com.fasterxml.jackson.databind.JsonNode;
import common.Translator;
import play.libs.Json;
import service.TranslationService;

public class JsonTranslationService
    implements
        TranslationService
{

	// --- NESTED TYPES -- //

	private static class TranslatorImpl
	    implements
	        Translator
	{

		private final Locale			locale;
		private HashMap<String, String>	map	= new HashMap<>();

		public TranslatorImpl(
		                      Locale l)
		{
			locale = l;
		}

		void add(
		    String key,
		    String value)
		{
			map.put(key, value);
		}

		@Override
		public String get(
		    String key,
		    Object... params)
		{
			String str = map.get(key);
			if (str == null)
			{
				if (params.length > 0)
				{
					return key + "@" + Arrays.toString(params);
				}
				return key;
			}
			return String.format(str, params);
		}

		@Override
		public Locale getLocale()
		{
			return locale;
		}

	}

	// --- FIELDS --- //

	HashMap<String, TranslatorImpl> translators = new HashMap<>();

	// --- CONSTRUCTORS --- //

	public JsonTranslationService()
	{
		// Nothing
	}

	// --- METHODS --- //

	public void load(
	    InputStream is)
	{
		translators = new HashMap<String, TranslatorImpl>();

		JsonNode root = Json.parse(is);

		root.fields()
		    .forEachRemaining((
		        entry) -> {
			    String key = entry.getKey();
			    JsonNode translations = entry.getValue();
			    translations.fields()
			                .forEachRemaining((
			                    translation) -> {
				                String lang = translation.getKey();
				                JsonNode node = translation.getValue();
				                String value = "";
				                if (node.isArray())
				                {
					                Iterator<JsonNode> lines = node.elements();
					                for (JsonNode l = lines.next(); lines.hasNext(); l = lines.next())
					                {
						                value += l.asText();
						                if (lines.hasNext())
						                {
							                value += "\n";
						                }
					                }
				                } else
				                {
					                value = translation.getValue()
					                                   .asText();
				                }

				                TranslatorImpl t = translators.get(lang);
				                if (t == null)
				                {
					                String[] parts = lang.split("_");
					                Locale l;
					                if (parts.length == 1)
					                {
						                l = new Locale(parts[0]);
					                } else if (parts.length == 2)
					                {
						                l = new Locale(parts[0], parts[1]);
					                } else
					                {
						                l = new Locale(lang);
					                }
					                t = new TranslatorImpl(l);
					                translators.put(lang, t);
				                }
				                t.add(key, value);
			                });
		    });
	}

	@Override
	public Translator forLocale(
	    Locale l)
	{
		String lang = l.getLanguage();
		String specificLang = lang + "_" + l.getCountry();

		Translator t = translators.get(specificLang);
		if (t != null)
		{
			return t;
		}

		t = translators.get(lang);
		if (t != null)
		{
			return t;
		}

		return TranslationService.DUMMY;
	}

}
