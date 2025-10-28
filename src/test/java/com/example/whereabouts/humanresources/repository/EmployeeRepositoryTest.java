package com.example.whereabouts.humanresources.repository;

import com.example.whereabouts.IntegrationTest;
import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.EmailAddress;
import com.example.whereabouts.common.Gender;
import com.example.whereabouts.common.PhoneNumber;
import com.example.whereabouts.common.address.*;
import com.example.whereabouts.humanresources.EmployeeData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    static EmployeeData createEmployeeData(int increment) {
        PostalAddress postalAddress = switch (increment % 5) {
            case 0 -> new InternationalPostalAddress("Street" + increment,
                    "City" + increment,
                    "State" + increment,
                    "Postal" + increment,
                    Country.isoCountries().get(increment % Country.isoCountries().size()));
            case 1 -> new FinnishPostalAddress("Street" + increment,
                    FinnishPostalCode.of(Integer.toString((10000 + increment) % 100000)),
                    "Post" + increment,
                    Country.ofIsoCode("FI"));
            case 2 -> new GermanPostalAddress("Street" + increment,
                    GermanPostalCode.of(Integer.toString((10000 + increment) % 100000)),
                    "City" + increment,
                    Country.ofIsoCode("DE"));
            case 3 -> new USPostalAddress("Street" + increment,
                    "City" + increment,
                    USState.values()[increment % USState.values().length],
                    USZipCode.of(Integer.toString((10000 + increment) % 100000)),
                    Country.ofIsoCode("US"));
            default -> new CanadianPostalAddress("Street" + increment, "City" + increment,
                    CanadianProvince.values()[increment % CanadianProvince.values().length],
                    CanadianPostalCode.of("A1A-2B2"), Country.ofIsoCode("CA"));
        };

        var zones = ZoneId.getAvailableZoneIds().stream().toList();
        var zone = ZoneId.of(zones.get(increment & zones.size()));

        return new EmployeeData("First" + increment,
                "Middle" + increment,
                "Last" + increment,
                "Preferred" + increment,
                LocalDate.of(1984, 2, 1).plusDays(increment),
                Gender.values()[increment % Gender.values().length],
                "Dietary" + increment,
                zone,
                postalAddress,
                PhoneNumber.of("+12301234" + increment),
                PhoneNumber.of("+12305678" + increment),
                PhoneNumber.of("+12309012" + increment),
                EmailAddress.of("email" + increment + "@work.foo")
        );
    }

    @Test
    void insert_get_and_update_include_all_properties() {
        var originalData = createEmployeeData(0);
        var id = repository.insert(originalData);
        var retrieved = repository.findById(id).orElseThrow();

        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);

        var updatedData = createEmployeeData(1);

        var updated = repository.update(retrieved.withData(updatedData));
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.data()).isEqualTo(updatedData);

        retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(2);
        assertThat(retrieved.data()).isEqualTo(updatedData);
    }

    @Test
    void update_uses_optimistic_locking() {
        var originalData = createEmployeeData(0);
        var id = repository.insert(originalData);
        var retrieved = repository.findById(id).orElseThrow();

        repository.update(retrieved);
        assertThatThrownBy(() -> repository.update(retrieved)).isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void update_affects_only_one_employee() {
        var originalData = createEmployeeData(0);
        var id = repository.insert(originalData);
        var retrieved = repository.findById(id).orElseThrow();

        var id1 = repository.insert(createEmployeeData(1));
        var id2 = repository.insert(createEmployeeData(2));

        var updatedData = createEmployeeData(3);
        repository.update(retrieved.withData(updatedData));

        assertThat(updatedData).isNotEqualTo(repository.findById(id1).orElseThrow().data());
        assertThat(updatedData).isNotEqualTo(repository.findById(id2).orElseThrow().data());
    }
}
