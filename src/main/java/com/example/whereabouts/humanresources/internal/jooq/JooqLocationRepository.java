package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.humanresources.Location;
import com.example.whereabouts.humanresources.LocationData;
import com.example.whereabouts.humanresources.LocationFacility;
import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.internal.LocationRepository;
import com.example.whereabouts.jooq.enums.FacilityType;
import com.example.whereabouts.jooq.tables.records.LocationFacilityRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static com.example.whereabouts.humanresources.internal.jooq.JooqConverters.zoneIdConverter;
import static com.example.whereabouts.jooq.Sequences.LOCATION_ID_SEQ;
import static com.example.whereabouts.jooq.tables.Location.LOCATION;
import static com.example.whereabouts.jooq.tables.LocationFacility.LOCATION_FACILITY;

@Component
@NullMarked
class JooqLocationRepository implements LocationRepository {

    private final DSLContext dsl;

    JooqLocationRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<Location> findById(LocationId id) {
        var FACILITIES = DSL.multiset(
                DSL.selectFrom(LOCATION_FACILITY)
                        .where(LOCATION_FACILITY.LOCATION_ID.eq(LOCATION.LOCATION_ID))
        );
        var TIME_ZONE = LOCATION.TIME_ZONE.convert(zoneIdConverter);
        return dsl
                .select(LOCATION.LOCATION_ID,
                        LOCATION.VERSION,
                        LOCATION.NAME,
                        LOCATION.LOCATION_TYPE,
                        LOCATION.POSTAL_ADDRESS,
                        LOCATION.ESTABLISHED,
                        LOCATION.ABOUT,
                        TIME_ZONE,
                        FACILITIES
                )
                .from(LOCATION)
                .where(LOCATION.LOCATION_ID.eq(id))
                .fetchOptional(record -> new Location(
                        record.getValue(LOCATION.LOCATION_ID),
                        record.getValue(LOCATION.VERSION),
                        new LocationData(
                                record.getValue(LOCATION.NAME),
                                record.getValue(LOCATION.LOCATION_TYPE),
                                record.getValue(LOCATION.POSTAL_ADDRESS),
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
        var id = new LocationId(dsl.nextval(LOCATION_ID_SEQ));
        dsl.insertInto(LOCATION)
                .set(LOCATION.LOCATION_ID, id)
                .set(LOCATION.VERSION, 1L)
                .set(LOCATION.NAME, locationData.name())
                .set(LOCATION.LOCATION_TYPE, locationData.locationType())
                .set(LOCATION.POSTAL_ADDRESS, locationData.address())
                .set(LOCATION.COUNTRY, locationData.address().country())
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
                .set(LOCATION.LOCATION_TYPE, location.data().locationType())
                .set(LOCATION.POSTAL_ADDRESS, location.data().address())
                .set(LOCATION.COUNTRY, location.data().address().country())
                .set(LOCATION.ESTABLISHED, location.data().established())
                .set(LOCATION.ABOUT, location.data().about())
                .set(LOCATION.TIME_ZONE, zoneIdConverter.to(location.data().timeZone()))
                .where(LOCATION.LOCATION_ID.eq(location.id()))
                .and(LOCATION.VERSION.eq(location.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Location was modified by another user");
        }

        dsl.deleteFrom(LOCATION_FACILITY)
                .where(LOCATION_FACILITY.LOCATION_ID.eq(location.id()))
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
                    record.setLocationId(locationId);
                    toRecord(record, facility);
                    return record;
                })
                .toList();

        dsl.batchInsert(batch).execute();
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
