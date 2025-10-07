package com.example.application.humanresources.location;

import com.example.application.jooq.enums.FacilityType;
import com.example.application.jooq.tables.records.LocationFacilityRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static com.example.application.humanresources.location.JooqLocationConverters.*;
import static com.example.application.jooq.Sequences.LOCATION_ID_SEQ;
import static com.example.application.jooq.tables.Location.LOCATION;
import static com.example.application.jooq.tables.LocationFacility.LOCATION_FACILITY;

/**
 * JOOQ-based implementation of {@link LocationRepository}.
 * <p>
 * This implementation provides type-safe SQL-based persistence for {@link Location} aggregate
 * roots using JOOQ's DSL. It handles bidirectional mapping between the domain model (immutable
 * records and sealed interfaces) and the relational database schema (tables and rows).
 * <p>
 * <strong>Database Schema</strong><br>
 * Locations are persisted across two tables:
 * <ul>
 *   <li>{@code location} - stores the location entity with all its attributes</li>
 *   <li>{@code location_facility} - stores facilities as child records with a composite
 *       primary key of (location_id, facility_type)</li>
 * </ul>
 * <p>
 * <strong>Optimistic Locking</strong><br>
 * Updates verify the version number hasn't changed before applying modifications, preventing
 * lost updates in concurrent scenarios. The version is incremented atomically during updates,
 * and mismatches throw {@link org.springframework.dao.OptimisticLockingFailureException}.
 * <p>
 * <strong>Facility Management</strong><br>
 * When updating locations, existing facilities are deleted and replaced with the new set
 * in a single transaction. This simple approach is appropriate given the small number of
 * facilities per location (typically under 10) and avoids the complexity of diffing and
 * selectively updating individual facility records.
 * <p>
 * <strong>Type Conversions</strong><br>
 * The implementation uses {@link JooqLocationConverters} for bidirectional conversion between
 * domain types and database types (location types, time zones, postal addresses). These
 * converters ensure consistent transformation logic across queries and persistence operations.
 * <p>
 * <strong>JOOQ Multiset</strong><br>
 * Queries use JOOQ's {@code multiset} feature to efficiently load locations with their
 * facilities in a single database round-trip, avoiding N+1 query problems while maintaining
 * type safety.
 *
 * @see LocationRepository
 * @see Location
 * @see LocationData
 * @see LocationFacility
 * @see JooqLocationConverters
 */
@Component
@NullMarked
class JooqLocationRepository implements LocationRepository {

    private final DSLContext dsl;

    JooqLocationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public boolean isEmpty() {
        return dsl.selectCount().from(LOCATION).fetchSingle().value1() == 0;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<Location> findById(LocationId id) {
        var LOCATION_TYPE = LOCATION.LOCATION_TYPE.convert(locationTypeConverter);
        var FACILITIES = DSL.multiset(
                DSL.selectFrom(LOCATION_FACILITY)
                        .where(LOCATION_FACILITY.LOCATION_ID.eq(LOCATION.LOCATION_ID))
        );
        var ADDRESS = LOCATION.ADDRESS.convert(postalAddressConverter);
        var TIME_ZONE = LOCATION.TIME_ZONE.convert(zoneIdConverter);
        return dsl
                .select(LOCATION.LOCATION_ID,
                        LOCATION.VERSION,
                        LOCATION.NAME,
                        LOCATION_TYPE,
                        ADDRESS,
                        LOCATION.ESTABLISHED,
                        LOCATION.ABOUT,
                        TIME_ZONE,
                        FACILITIES
                )
                .from(LOCATION)
                .where(LOCATION.LOCATION_ID.eq(id.toLong()))
                .fetchOptional(record -> new Location(
                        LocationId.of(record.getValue(LOCATION.LOCATION_ID)),
                        record.getValue(LOCATION.VERSION),
                        new LocationData(
                                record.getValue(LOCATION.NAME),
                                record.getValue(LOCATION_TYPE),
                                record.getValue(ADDRESS),
                                record.getValue(LOCATION.ESTABLISHED),
                                record.getValue(LOCATION.ABOUT),
                                record.getValue(TIME_ZONE),
                                record.getValue(FACILITIES).map(this::fromRecord)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public LocationId insert(LocationData locationData) {
        var id = LocationId.of(dsl.nextval(LOCATION_ID_SEQ));
        dsl.insertInto(LOCATION)
                .set(LOCATION.LOCATION_ID, id.toLong())
                .set(LOCATION.VERSION, 1L)
                .set(LOCATION.NAME, locationData.name())
                .set(LOCATION.LOCATION_TYPE, locationTypeConverter.to(locationData.locationType()))
                .set(LOCATION.ADDRESS, postalAddressConverter.to(locationData.address()))
                .set(LOCATION.COUNTRY, locationData.address().country().isoCode())
                .set(LOCATION.ESTABLISHED, locationData.established())
                .set(LOCATION.ABOUT, locationData.about())
                .set(LOCATION.TIME_ZONE, zoneIdConverter.to(locationData.timeZone()))
                .execute();

        insertFacilities(id, locationData.facilities());
        return id;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Location update(Location location) {
        var newVersion = location.version() + 1;
        var rowsUpdated = dsl.update(LOCATION)
                .set(LOCATION.VERSION, newVersion)
                .set(LOCATION.NAME, location.data().name())
                .set(LOCATION.LOCATION_TYPE, locationTypeConverter.to(location.data().locationType()))
                .set(LOCATION.ADDRESS, postalAddressConverter.to(location.data().address()))
                .set(LOCATION.COUNTRY, location.data().address().country().isoCode())
                .set(LOCATION.ESTABLISHED, location.data().established())
                .set(LOCATION.ABOUT, location.data().about())
                .set(LOCATION.TIME_ZONE, zoneIdConverter.to(location.data().timeZone()))
                .where(LOCATION.LOCATION_ID.eq(location.id().toLong()))
                .and(LOCATION.VERSION.eq(location.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Location was modified by another user");
        }

        dsl.deleteFrom(LOCATION_FACILITY)
                .where(LOCATION_FACILITY.LOCATION_ID.eq(location.id().toLong()))
                .execute();

        insertFacilities(location.id(), location.data().facilities());

        return new Location(location.id(), newVersion, location.data());
    }

    private void insertFacilities(LocationId locationId, Collection<LocationFacility> facilities) {
        if (facilities.isEmpty()) {
            return;
        }

        var batch = facilities.stream()
                .map(facility -> {
                    var record = dsl.newRecord(LOCATION_FACILITY);
                    record.setLocationId(locationId.toLong());
                    toRecord(record, facility);
                    return record;
                })
                .toList();

        dsl.batchInsert(batch).execute();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteById(LocationId locationId) {
        dsl.deleteFrom(LOCATION_FACILITY)
                .where(LOCATION.LOCATION_ID.eq(locationId.toLong()))
                .execute();
        dsl.deleteFrom(LOCATION)
                .where(LOCATION.LOCATION_ID.eq(locationId.toLong()))
                .execute();
    }

    private LocationFacility fromRecord(LocationFacilityRecord record) {
        return switch (record.getFacilityType()) {
            case ACCESSIBLE_OFFICE -> new LocationFacility.AccessibleOffice();
            case FLOOR_SPACE -> new LocationFacility.FloorSpace(record.getQuantity());
            case HOT_DESKS -> new LocationFacility.HotDesks(record.getQuantity());
            case KITCHEN -> new LocationFacility.Kitchen();
            case MEETING_BOOTHS -> new LocationFacility.MeetingBooths(record.getQuantity());
            case PARKING_SLOTS -> new LocationFacility.ParkingSlots(record.getQuantity());
        };
    }

    private void toRecord(LocationFacilityRecord record, LocationFacility locationFacility) {
        switch (locationFacility) {
            case LocationFacility.AccessibleOffice ignored -> {
                record.setFacilityType(FacilityType.ACCESSIBLE_OFFICE);
                record.setQuantity(1);
            }
            case LocationFacility.FloorSpace floorSpace -> {
                record.setFacilityType(FacilityType.FLOOR_SPACE);
                record.setQuantity(floorSpace.squareMeters());
            }
            case LocationFacility.HotDesks hotDesks -> {
                record.setFacilityType(FacilityType.HOT_DESKS);
                record.setQuantity(hotDesks.number());
            }
            case LocationFacility.Kitchen ignored -> {
                record.setFacilityType(FacilityType.KITCHEN);
                record.setQuantity(1);
            }
            case LocationFacility.MeetingBooths meetingBooths -> {
                record.setFacilityType(FacilityType.MEETING_BOOTHS);
                record.setQuantity(meetingBooths.number());
            }
            case LocationFacility.ParkingSlots parkingSlots -> {
                record.setFacilityType(FacilityType.PARKING_SLOTS);
                record.setQuantity(parkingSlots.number());
            }
        }
    }
}
