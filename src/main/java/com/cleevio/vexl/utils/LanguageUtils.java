package com.cleevio.vexl.utils;

import java.util.Locale;
import java.util.Optional;

public class LanguageUtils {

	/**
	 * Parse language with highest priority from Accept-Language header
	 *
	 * @param header Accept-Language header value in
	 * @return Language (or null if not detected)
	 */
	public static String fromHeader(String header) {
		return Optional.ofNullable(header)
				.map(h -> Locale.LanguageRange.parse(header))
				.filter(l -> l.size() > 0)
				// Get language with most priority
				.map(l -> l.get(0))
				.map(Locale.LanguageRange::getRange)
				// Allow only languages in ISO 639-1
				.filter(l -> l.length() == 2)
				.orElse(null);
	}

	/**
	 * Convert string representation of locale to locale
	 *
	 * @param locale String locale
	 * @return Locale
	 */
	public static Locale toLocale(String locale) {
		return Optional.ofNullable(locale).map(Locale::forLanguageTag).orElse(Locale.getDefault());
	}

}
