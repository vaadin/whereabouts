package com.example.application.humanresources.employee;

import com.example.application.TestcontainersConfiguration;
import com.example.application.common.Country;
import com.example.application.common.EmailAddress;
import com.example.application.common.Gender;
import com.example.application.common.PhoneNumber;
import com.example.application.common.address.FinnishPostalAddress;
import com.example.application.common.address.FinnishPostalCode;
import com.example.application.common.address.InternationalPostalAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@ActiveProfiles("integration-test")
class JooqEmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    @Test
    void insert_get_and_update_includes_all_properties() {
        var originalData = new EmployeeData("First",
                "Middle",
                "Last",
                "Preferred",
                LocalDate.of(1984, 2, 1),
                Gender.OTHER,
                "Dietary",
                ZoneId.of("UTC"),
                new FinnishPostalAddress(
                        "Street",
                        FinnishPostalCode.of("12345"),
                        "Post",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234567"),
                PhoneNumber.of("+358509876543"),
                PhoneNumber.of("+358441357900"),
                EmailAddress.of("email@work.foo")
        );
        var id = repository.insert(originalData);
        var retrieved = repository.findById(id).orElseThrow();

        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);

        var updatedData = new EmployeeData("First2",
                "Middle2",
                "Last2",
                "Preferred2",
                LocalDate.of(1985, 3, 2),
                Gender.MALE,
                "Dietary2",
                ZoneId.of("Europe/Stockholm"),
                new InternationalPostalAddress(
                        "Street2",
                        "City2",
                        null,
                        "23456",
                        Country.ofIsoCode("SE")
                ),
                PhoneNumber.of("0401234567"),
                PhoneNumber.of("0509876543"),
                PhoneNumber.of("0441357900"),
                EmailAddress.of("email2@work.foo")
        );

        var updated = repository.update(retrieved.withData(updatedData));
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.data()).isEqualTo(updatedData);

        assertThat(repository.findById(id)).contains(updated);
    }

}
