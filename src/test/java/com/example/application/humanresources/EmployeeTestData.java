package com.example.application.humanresources;

import com.example.application.common.Country;
import com.example.application.common.EmailAddress;
import com.example.application.common.Gender;
import com.example.application.common.PhoneNumber;
import com.example.application.common.address.InternationalPostalAddress;
import com.example.application.humanresources.internal.EmployeeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class EmployeeTestData {

    private final Random rnd = new Random();
    private final EmployeeRepository employeeRepository;

    EmployeeTestData(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeId createEmployee() {
        var r = rnd.nextInt();
        return employeeRepository.insert(new EmployeeData(
                "First" + r,
                "Middle" + r,
                "Last" + r,
                "Preferred" + r,
                LocalDate.of(1983, 12, 31),
                pickRandom(Gender.values()),
                "Dietary" + r,
                ZoneId.of(pickRandom(ZoneId.getAvailableZoneIds())),
                new InternationalPostalAddress("Street" + r, "City" + r, "State" + r, "Postal" + r, pickRandom(Country.isoCountries())),
                PhoneNumber.of("12345678"),
                PhoneNumber.of("23456789"),
                PhoneNumber.of("34567890"),
                EmailAddress.of("email" + r + "@foo.bar")
        ));
    }

    private <T> T pickRandom(T[] values) {
        return values[rnd.nextInt(values.length)];
    }

    private <T> T pickRandom(List<T> items) {
        return items.get(rnd.nextInt(items.size()));
    }

    private <T> T pickRandom(Set<T> items) {
        return items.stream().sorted().toList().get(rnd.nextInt(items.size()));
    }
}
