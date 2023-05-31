package rs.edu.raf.si.bank2.otc.bootstrap.readers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

public class CountryCodeMapper {
    private static final Map<String, String> COUNTRY_CODE_MAP = new HashMap<>();

    static {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            try {
                String countryCode = locale.getISO3Country();
                String countryName = locale.getDisplayCountry();
                COUNTRY_CODE_MAP.put(countryName.toLowerCase(), countryCode);
            } catch (MissingResourceException e) {
                // Ignore locales without country codes
            }
        }
    }

    public static String getCountryCode(String countryName) {
        return COUNTRY_CODE_MAP.get(countryName.toLowerCase());
    }
}
