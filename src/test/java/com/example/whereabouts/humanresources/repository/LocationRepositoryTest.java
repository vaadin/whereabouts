package com.example.whereabouts.humanresources.repository;

import com.example.whereabouts.IntegrationTest;
import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.address.FinnishPostalAddress;
import com.example.whereabouts.common.address.FinnishPostalCode;
import com.example.whereabouts.common.address.InternationalPostalAddress;
import com.example.whereabouts.humanresources.LocationData;
import com.example.whereabouts.humanresources.LocationFacility;
import com.example.whereabouts.humanresources.LocationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class LocationRepositoryTest {

    @Autowired
    LocationRepository repository;

    static LocationData createLocationData() {
        return new LocationData(
                "Name",
                LocationType.REGIONAL_HQ,
                new FinnishPostalAddress(
                        "Street",
                        FinnishPostalCode.of("12345"),
                        "Post",
                        Country.ofIsoCode("FI")
                ),
                LocalDate.of(2016, 6, 21),
                "About",
                ZoneId.of("Europe/Helsinki"),
                List.of(
                        new LocationFacility.AccessibleOffice(),
                        new LocationFacility.FloorSpace(200),
                        new LocationFacility.Kitchen(),
                        new LocationFacility.ParkingSlots(10)
                )
        );
    }

    @Test
    void insert_get_and_update_include_all_properties() {
        var originalData = createLocationData();
        var id = repository.insert(originalData);

        var retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);

        var updatedData = new LocationData(
                "Name2",
                LocationType.REMOTE_HUB,
                new InternationalPostalAddress(
                        "Street2",
                        "City2",
                        null,
                        "23456",
                        Country.ofIsoCode("SE")
                ),
                LocalDate.of(2017, 7, 22),
                "About2",
                ZoneId.of("Europe/Stockholm"),
                List.of(
                        new LocationFacility.AccessibleOffice(),
                        new LocationFacility.FloorSpace(150),
                        new LocationFacility.MeetingBooths(10)
                )
        );

        var updated = repository.update(retrieved.withData(updatedData));
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.data()).isEqualTo(updatedData);

        retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(2);
        assertThat(retrieved.data()).isEqualTo(updatedData);
    }
}
