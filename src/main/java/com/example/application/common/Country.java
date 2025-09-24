package com.example.application.common;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Domain primitive representing a country.
 */
@NullMarked
public final class Country implements Serializable {

    private static final List<Country> ISO_COUNTRIES;

    static {
        ISO_COUNTRIES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).stream().map(Country::ofIsoCode).toList();
    }

    private final Locale locale;

    private Country(Locale locale) {
        this.locale = requireNonNull(locale);
    }

    /**
     * Gets the locale of the country.
     *
     * @return the locale.
     */
    public Locale locale() {
        return locale;
    }

    /**
     * Gets the ISO 3166 code of the country,
     *
     * @return the ISO code.
     */
    public String isoCode() {
        return locale.getCountry();
    }

    /**
     * Gets the display name of the country.
     *
     * @return the display name of the country.
     */
    public String displayName() {
        return displayName(null);
    }

    /**
     * The display name of the country, in the given locale if applicable.
     *
     * @param displayIn the locale to display the country's name in, if relevant.
     * @return the display name of the country.
     */
    public String displayName(@Nullable Locale displayIn) {
        return locale.getDisplayCountry(displayIn == null ? Locale.getDefault() : displayIn);
    }

    @Override
    public String toString() {
        return locale.getCountry();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (Country) o;
        return Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locale);
    }

    /**
     * Checks if the given locale is a valid country,
     *
     * @param locale the locale to check.
     * @return {@code true} if the locale is a valid country, {@code false} otherwise.
     */
    public static boolean isValid(Locale locale) {
        var countryName = locale.getDisplayCountry();
        return !countryName.isBlank() && !countryName.equals(locale.getCountry());
    }

    /**
     * Creates a new {@code Country} from the given locale.
     *
     * @param locale the locale of the country to create.
     * @return the new {@code Country}.
     * @throws IllegalArgumentException if the given locale does not represent a valid country.
     */
    public static Country ofLocale(Locale locale) {
        if (!isValid(locale)) {
            throw new IllegalArgumentException("Locale does not represent a country");
        }
        return new Country(locale);
    }

    /**
     * Creates a new {@code Country} from the given ISO 3166 code.
     *
     * @param isoCode the ISO code of the country to create.
     * @return the new {@code Country}.
     * @throws IllegalArgumentException if the given ISO code is not valid.
     */
    public static Country ofIsoCode(String isoCode) {
        return ofLocale(Locale.of("", isoCode));
    }

    /**
     * A list of all ISO countries provided by the current Java VM.
     */
    public static List<Country> isoCountries() {
        return ISO_COUNTRIES;
    }
}