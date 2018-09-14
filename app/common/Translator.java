package common;

import java.util.Locale;

public interface Translator
{
	public String get(
	    String key,
	    Object... params);

	public Locale getLocale();
}
