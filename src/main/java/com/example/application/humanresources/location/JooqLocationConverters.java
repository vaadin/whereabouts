package com.example.application.humanresources.location;

import com.example.application.common.address.JooqPostalAddressConverter;
import com.example.application.common.address.PostalAddress;
import org.jooq.Converter;

import java.time.ZoneId;

/**
 * Shared JOOQ converters for {@link Location}-related database operations.
 * <p>
 * This utility class provides bidirectional type converters that transform between domain
 * types used in the {@link Location} aggregate and their database representations. These
 * converters ensure consistent, type-safe conversion logic across all queries and persistence
 * operations involving locations.
 * <p>
 * <strong>Why Shared Converters?</strong><br>
 * Location data is accessed through multiple paths:
 * <ul>
 *   <li>{@link LocationRepository} - for full CRUD operations on locations</li>
 *   <li>Query classes - for specialized UI queries (lists, filters, projections)</li>
 *   <li>Report generation - for analytics and exports</li>
 * </ul>
 * Centralizing converters in this class ensures all database access uses identical conversion
 * logic, preventing subtle bugs from inconsistent transformations and reducing duplication.
 * <p>
 * <strong>Converter Types</strong><br>
 * All converters are stateless and thread-safe, suitable for use as static final constants:
 * <ul>
 *   <li>{@link #postalAddressConverter} - JSON ↔ PostalAddress record</li>
 *   <li>{@link #zoneIdConverter} - String ↔ ZoneId</li>
 *   <li>{@link #locationTypeConverter} - Database enum ↔ Domain enum</li>
 * </ul>
 * <p>
 * <strong>Usage Pattern</strong><br>
 * Import converters statically in JOOQ code for clean, readable queries:
 * <pre>{@code
 * var TIME_ZONE = LOCATION.TIME_ZONE.convert(zoneIdConverter);
 * var ADDRESS = LOCATION.ADDRESS.convert(postalAddressConverter);
 * }</pre>
 *
 * @see Location
 * @see LocationData
 * @see org.jooq.Converter
 */
final class JooqLocationConverters {

    private JooqLocationConverters() {
        // Prevent instantiation - utility class with static members only
    }

    /**
     * Converts between database JSON representation and {@link PostalAddress} domain objects.
     * <p>
     * Postal addresses are stored as JSONB in PostgreSQL to accommodate the varying structure
     * of addresses across different countries. This converter handles serialization and
     * deserialization using Jackson.
     */
    public static final JooqPostalAddressConverter postalAddressConverter = new JooqPostalAddressConverter();

    /**
     * Converts between database timezone strings and Java {@link ZoneId} objects.
     * <p>
     * Time zones are stored as strings (e.g., "Europe/Stockholm") in the database and
     * converted to ZoneId for type-safe manipulation in the domain layer. This converter
     * uses {@link ZoneId#of(String)} for parsing and {@link ZoneId#getId()} for formatting.
     */
    public static final Converter<String, ZoneId> zoneIdConverter = Converter.ofNullable(
            String.class, ZoneId.class, ZoneId::of, ZoneId::getId
    );

    /**
     * Converts between JOOQ-generated database enum and domain {@link LocationType} enum.
     * <p>
     * Both enums have identical constant names, allowing simple name-based conversion via
     * {@link Enum#valueOf(Class, String)}. This maintains type safety while keeping the
     * domain model independent of JOOQ-generated code.
     */
    public static final Converter<com.example.application.jooq.enums.LocationType, LocationType> locationTypeConverter = Converter.ofNullable(
            com.example.application.jooq.enums.LocationType.class,
            LocationType.class,
            dbType -> LocationType.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.LocationType.valueOf(domainType.name())
    );
}
