package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.EmailAddress;
import com.example.whereabouts.common.Gender;
import com.example.whereabouts.common.PhoneNumber;
import com.example.whereabouts.common.address.*;
import com.example.whereabouts.humanresources.repository.EmployeeRepository;
import com.example.whereabouts.humanresources.repository.EmploymentDetailsRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@NullMarked
public class EmployeeTestDataService {

    private final Random rnd = new Random();
    private final EmployeeRepository employeeRepository;
    private final EmploymentDetailsRepository employmentDetailsRepository;
    private final LocationTestDataService locationTestDataService;

    EmployeeTestDataService(EmployeeRepository employeeRepository, EmploymentDetailsRepository employmentDetailsRepository,
                            LocationTestDataService locationTestDataService) {
        this.employeeRepository = employeeRepository;
        this.employmentDetailsRepository = employmentDetailsRepository;
        this.locationTestDataService = locationTestDataService;
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
        if (values.length == 0) {
            throw new IllegalArgumentException("Cannot pick from empty array");
        }
        return values[rnd.nextInt(values.length)];
    }

    private <T> T pickRandom(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from empty list");
        }
        return items.get(rnd.nextInt(items.size()));
    }

    private <T> T pickRandom(Set<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from empty set");
        }
        return items.stream().sorted().toList().get(rnd.nextInt(items.size()));
    }

    @Transactional
    public void createTestEmployees() {
        // Finland
        insert(new EmployeeData(
                "Mikko",
                "Tapani",
                "Korhonen",
                "Mikko",
                LocalDate.of(1985, 3, 14),
                Gender.MALE,
                "Vegetarian",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Kalevankatu 12 A 4",
                        FinnishPostalCode.of("00100"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234501"),
                PhoneNumber.of("+358441234501"),
                null,
                EmailAddress.of("mikko.korhonen@company.com")
        ));

        insert(new EmployeeData(
                "Anna",
                "Maria",
                "Lehtinen",
                "Anna",
                LocalDate.of(1990, 7, 22),
                Gender.FEMALE,
                "Gluten-free",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Runeberginkatu 45 B 7",
                        FinnishPostalCode.of("00260"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234502"),
                PhoneNumber.of("+358441234502"),
                null,
                EmailAddress.of("anna.lehtinen@company.com")
        ));
        insert(new EmployeeData(
                "Jari",
                "Olavi",
                "Virtanen",
                "Jari",
                LocalDate.of(1978, 12, 2),
                Gender.MALE,
                "No seafood",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Hämeentie 88",
                        FinnishPostalCode.of("00550"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234503"),
                PhoneNumber.of("+358441234503"),
                null,
                EmailAddress.of("jari.virtanen@company.com")
        ));
        insert(new EmployeeData(
                "Sanna",
                "Elina",
                "Miettinen",
                "Sanna",
                LocalDate.of(1989, 5, 5),
                Gender.FEMALE,
                "Vegan",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Mechelininkatu 23",
                        FinnishPostalCode.of("00100"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234504"),
                PhoneNumber.of("+358441234504"),
                null,
                EmailAddress.of("sanna.miettinen@company.com")
        ));
        insert(new EmployeeData(
                "Oskari",
                "Juhani",
                "Niemi",
                "Oskari",
                LocalDate.of(1995, 11, 19),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Lauttasaarentie 9 C 3",
                        FinnishPostalCode.of("00200"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234505"),
                PhoneNumber.of("+358441234505"),
                null,
                EmailAddress.of("oskari.niemi@company.com")
        ));
        insert(new EmployeeData(
                "Laura",
                "Helena",
                "Koskinen",
                "Laura",
                LocalDate.of(1982, 2, 28),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Mannerheimintie 90",
                        FinnishPostalCode.of("00270"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234506"),
                PhoneNumber.of("+358441234506"),
                null,
                EmailAddress.of("laura.koskinen@company.com")
        ));
        insert(new EmployeeData(
                "Petri",
                "Johannes",
                "Salmi",
                "Petri",
                LocalDate.of(1987, 8, 10),
                Gender.MALE,
                "Peanut allergy",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Itämerenkatu 14",
                        FinnishPostalCode.of("00180"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234507"),
                PhoneNumber.of("+358441234507"),
                null,
                EmailAddress.of("petri.salmi@company.com")
        ));
        insert(new EmployeeData(
                "Emilia",
                "Sofia",
                "Räsänen",
                "Emilia",
                LocalDate.of(1993, 6, 30),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Pohjoisranta 3",
                        FinnishPostalCode.of("00170"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234508"),
                PhoneNumber.of("+358441234508"),
                null,
                EmailAddress.of("emilia.rasanen@company.com")
        ));
        insert(new EmployeeData(
                "Ville",
                "Kalevi",
                "Laine",
                "Ville",
                LocalDate.of(1980, 9, 18),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Fredrikinkatu 22 A",
                        FinnishPostalCode.of("00120"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234509"),
                PhoneNumber.of("+358441234509"),
                null,
                EmailAddress.of("ville.laine@company.com")
        ));
        insert(new EmployeeData(
                "Noora",
                "Kristiina",
                "Heikkilä",
                "Noora",
                LocalDate.of(1998, 1, 25),
                Gender.FEMALE,
                "Vegan, no soy",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Töölönkatu 11",
                        FinnishPostalCode.of("00260"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234510"),
                PhoneNumber.of("+358441234510"),
                null,
                EmailAddress.of("noora.heikkila@company.com")
        ));
        insert(new EmployeeData(
                "Alex",
                "Mikael",
                "Johansson",
                "Alex",
                LocalDate.of(1992, 4, 12),
                Gender.OTHER,
                "Vegan, prefers oat milk",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Tehtaankatu 6",
                        FinnishPostalCode.of("00140"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234511"),
                PhoneNumber.of("+358441234511"),
                null,
                EmailAddress.of("alex.johansson@company.com")
        ));
        insert(new EmployeeData(
                "Sam",
                "Leevi",
                "Nguyen",
                "Sam",
                LocalDate.of(1997, 10, 3),
                Gender.OTHER,
                "Pescatarian",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Korkeavuorenkatu 28",
                        FinnishPostalCode.of("00130"),
                        "Helsinki",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401234512"),
                PhoneNumber.of("+358441234512"),
                null,
                EmailAddress.of("sam.nguyen@company.com")
        ));
        insert(new EmployeeData(
                "Eero",
                "Juhani",
                "Lehto",
                "Eero",
                LocalDate.of(1986, 2, 11),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Yliopistonkatu 18 B 3",
                        FinnishPostalCode.of("20100"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235001"),
                PhoneNumber.of("+358441235001"),
                null,
                EmailAddress.of("eero.lehto@company.com")
        ));
        insert(new EmployeeData(
                "Linnea",
                "Kristina",
                "Sundström",
                "Linnea",
                LocalDate.of(1991, 9, 3),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Itäinen Rantakatu 6",
                        FinnishPostalCode.of("20100"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235002"),
                PhoneNumber.of("+358441235002"),
                null,
                EmailAddress.of("linnea.sundstrom@company.com")
        ));
        insert(new EmployeeData(
                "Amir",
                "Hassan",
                "Al-Mansur",
                "Amir",
                LocalDate.of(1984, 5, 27),
                Gender.MALE,
                "Halal meals preferred",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Linnankatu 58",
                        FinnishPostalCode.of("20100"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235003"),
                PhoneNumber.of("+358441235003"),
                null,
                EmailAddress.of("amir.almansur@company.com")
        ));
        insert(new EmployeeData(
                "Mai",
                "Thi",
                "Nguyen",
                "Mai",
                LocalDate.of(1996, 12, 9),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Hämeenkatu 22 A 5",
                        FinnishPostalCode.of("20500"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235004"),
                PhoneNumber.of("+358441235004"),
                null,
                EmailAddress.of("mai.nguyen@company.com")
        ));
        insert(new EmployeeData(
                "Noora",
                "Katariina",
                "Hakala",
                "Noora",
                LocalDate.of(1998, 4, 15),
                Gender.FEMALE,
                "Gluten-free",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Kartanontie 7",
                        FinnishPostalCode.of("20780"),
                        "Kaarina",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235005"),
                PhoneNumber.of("+358441235005"),
                null,
                EmailAddress.of("noora.hakala@company.com")
        ));
        insert(new EmployeeData(
                "Ivan",
                "Sergei",
                "Petrov",
                "Ivan",
                LocalDate.of(1981, 1, 4),
                Gender.MALE,
                "No seafood",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Tikkumäentie 3",
                        FinnishPostalCode.of("21200"),
                        "Raisio",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235006"),
                PhoneNumber.of("+358441235006"),
                null,
                EmailAddress.of("ivan.petrov@company.com")
        ));
        insert(new EmployeeData(
                "Kaisa",
                "Marleen",
                "Tamm",
                "Kaisa",
                LocalDate.of(1993, 7, 20),
                Gender.FEMALE,
                "None",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Käsityöläiskatu 9",
                        FinnishPostalCode.of("21100"),
                        "Naantali",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235007"),
                PhoneNumber.of("+358441235007"),
                null,
                EmailAddress.of("kaisa.tamm@company.com")
        ));
        insert(new EmployeeData(
                "João",
                "Miguel",
                "Silva",
                "Joao",
                LocalDate.of(1987, 6, 2),
                Gender.MALE,
                "Pescatarian",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Ilmaristentie 15",
                        FinnishPostalCode.of("21420"),
                        "Lieto",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235008"),
                PhoneNumber.of("+358441235008"),
                null,
                EmailAddress.of("joao.silva@company.com")
        ));
        insert(new EmployeeData(
                "Aisha",
                "Fatima",
                "Khan",
                "Aisha",
                LocalDate.of(1994, 3, 8),
                Gender.FEMALE,
                "Vegetarian, no eggs",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Skanssinkatu 10",
                        FinnishPostalCode.of("20730"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235009"),
                PhoneNumber.of("+358441235009"),
                null,
                EmailAddress.of("aisha.khan@company.com")
        ));
        insert(new EmployeeData(
                "Alex",
                "Mika",
                "Korventausta",
                "Alex",
                LocalDate.of(1992, 11, 1),
                Gender.OTHER,
                "Vegan, soy OK",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Aninkaistenkatu 4",
                        FinnishPostalCode.of("20100"),
                        "Turku",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235010"),
                PhoneNumber.of("+358441235010"),
                null,
                EmailAddress.of("alex.korventausta@company.com")
        ));
        insert(new EmployeeData(
                "Robin",
                "Alvar",
                "Sjöberg",
                "Robin",
                LocalDate.of(1989, 10, 14),
                Gender.OTHER,
                "Peanut allergy",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Kuloistentie 2 B",
                        FinnishPostalCode.of("21200"),
                        "Raisio",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235011"),
                PhoneNumber.of("+358441235011"),
                null,
                EmailAddress.of("robin.sjoberg@company.com")
        ));
        insert(new EmployeeData(
                "Yasmin",
                "Hodan",
                "Warsame",
                "Yasmin",
                LocalDate.of(1997, 8, 26),
                Gender.FEMALE,
                "No pork",
                ZoneId.of("Europe/Helsinki"),
                new FinnishPostalAddress(
                        "Auranlaaksonkuja 1",
                        FinnishPostalCode.of("20780"),
                        "Kaarina",
                        Country.ofIsoCode("FI")
                ),
                PhoneNumber.of("+358401235012"),
                PhoneNumber.of("+358441235012"),
                null,
                EmailAddress.of("yasmin.warsame@company.com")
        ));

        // Germany
        insert(new EmployeeData(
                "Lukas",
                "Johann",
                "Müller",
                "Lukas",
                LocalDate.of(1987, 5, 12),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Friedrichstraße 123",
                        GermanPostalCode.of("10117"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234501"),
                PhoneNumber.of("+491511234501"),
                PhoneNumber.of("+49301111222"),
                EmailAddress.of("lukas.mueller@company.com")
        ));
        insert(new EmployeeData(
                "Sophie",
                "Maria",
                "Schneider",
                "Sophie",
                LocalDate.of(1991, 11, 4),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Kastanienallee 45",
                        GermanPostalCode.of("10435"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234502"),
                PhoneNumber.of("+491511234502"),
                PhoneNumber.of("+49301111223"),
                EmailAddress.of("sophie.schneider@company.com")
        ));
        insert(new EmployeeData(
                "Emre",
                "Can",
                "Yılmaz",
                "Emre",
                LocalDate.of(1989, 3, 8),
                Gender.MALE,
                "Halal meals only",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Karl-Marx-Allee 78",
                        GermanPostalCode.of("10243"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234503"),
                PhoneNumber.of("+491511234503"),
                PhoneNumber.of("+49301111224"),
                EmailAddress.of("emre.yilmaz@company.com")
        ));
        insert(new EmployeeData(
                "Magda",
                "Ewa",
                "Kowalska",
                "Magda",
                LocalDate.of(1993, 9, 17),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Warschauer Straße 25",
                        GermanPostalCode.of("10243"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234504"),
                PhoneNumber.of("+491511234504"),
                PhoneNumber.of("+49301111225"),
                EmailAddress.of("magda.kowalska@company.com")
        ));
        insert(new EmployeeData(
                "Oliver",
                "James",
                "Brown",
                "Oli",
                LocalDate.of(1984, 8, 29),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Torstraße 11",
                        GermanPostalCode.of("10119"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234505"),
                PhoneNumber.of("+491511234505"),
                PhoneNumber.of("+49301111226"),
                EmailAddress.of("oliver.brown@company.com")
        ));
        insert(new EmployeeData(
                "Claire",
                "Louise",
                "Dubois",
                "Claire",
                LocalDate.of(1990, 7, 14),
                Gender.FEMALE,
                "Vegan",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Potsdamer Platz 3",
                        GermanPostalCode.of("10785"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234506"),
                PhoneNumber.of("+491511234506"),
                PhoneNumber.of("+49301111227"),
                EmailAddress.of("claire.dubois@company.com")
        ));
        insert(new EmployeeData(
                "Alex",
                "Robin",
                "Krämer",
                "Alex",
                LocalDate.of(1995, 12, 21),
                Gender.OTHER,
                "Gluten-free, vegan",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Schönhauser Allee 132",
                        GermanPostalCode.of("10437"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234507"),
                PhoneNumber.of("+491511234507"),
                PhoneNumber.of("+49301111228"),
                EmailAddress.of("alex.kraemer@company.com")
        ));
        insert(new EmployeeData(
                "Adrián",
                "Santos",
                "López",
                "Adri",
                LocalDate.of(1996, 6, 6),
                Gender.OTHER,
                "Vegetarian, prefers oat milk",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Tempelhofer Ufer 18",
                        GermanPostalCode.of("10963"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234508"),
                PhoneNumber.of("+491511234508"),
                PhoneNumber.of("+49301111229"),
                EmailAddress.of("adrian.lopez@company.com")
        ));
        insert(new EmployeeData(
                "Giulia",
                "Rosa",
                "Moretti",
                "Giulia",
                LocalDate.of(1988, 1, 30),
                Gender.FEMALE,
                "Pescatarian",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Kurfürstendamm 75",
                        GermanPostalCode.of("10709"),
                        "Berlin",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234509"),
                PhoneNumber.of("+491511234509"),
                PhoneNumber.of("+49301111230"),
                EmailAddress.of("giulia.moretti@company.com")
        ));
        insert(new EmployeeData(
                "Jonas",
                "Matthias",
                "Becker",
                "Jonas",
                LocalDate.of(1997, 2, 8),
                Gender.MALE,
                "No pork",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Rudolf-Breitscheid-Straße 10",
                        GermanPostalCode.of("14482"),
                        "Potsdam",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49301234510"),
                PhoneNumber.of("+491511234510"),
                PhoneNumber.of("+49301111231"),
                EmailAddress.of("jonas.becker@company.com")
        ));
        insert(new EmployeeData(
                "Sebastian",
                "Karl",
                "Wagner",
                "Sebastian",
                LocalDate.of(1986, 4, 3),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Marienplatz 8",
                        GermanPostalCode.of("80331"),
                        "München",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+498912345001"),
                PhoneNumber.of("+491511700001"),
                PhoneNumber.of("+498932100001"),
                EmailAddress.of("sebastian.wagner@company.com")
        ));
        insert(new EmployeeData(
                "Elena",
                "Maria",
                "Schäfer",
                "Elena",
                LocalDate.of(1991, 1, 19),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Zeil 72",
                        GermanPostalCode.of("60311"),
                        "Frankfurt am Main",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+496912345002"),
                PhoneNumber.of("+491511700002"),
                PhoneNumber.of("+496969100002"),
                EmailAddress.of("elena.schaefer@company.com")
        ));
        insert(new EmployeeData(
                "Can",
                "Emre",
                "Öztürk",
                "Can",
                LocalDate.of(1989, 9, 7),
                Gender.MALE,
                "Halal meals preferred",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Spitalerstraße 3",
                        GermanPostalCode.of("20095"),
                        "Hamburg",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+494012345003"),
                PhoneNumber.of("+491511700003"),
                PhoneNumber.of("+494040100003"),
                EmailAddress.of("can.oeztuerk@company.com")
        ));
        insert(new EmployeeData(
                "Julia",
                "Anne",
                "Keller",
                "Julia",
                LocalDate.of(1993, 6, 15),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Hohe Straße 10",
                        GermanPostalCode.of("50667"),
                        "Köln",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+492212345004"),
                PhoneNumber.of("+491511700004"),
                PhoneNumber.of("+492219900004"),
                EmailAddress.of("julia.keller@company.com")
        ));
        insert(new EmployeeData(
                "Matteo",
                "Luca",
                "Rossi",
                "Matteo",
                LocalDate.of(1984, 11, 28),
                Gender.MALE,
                "Pescatarian",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Königstraße 45",
                        GermanPostalCode.of("70173"),
                        "Stuttgart",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+497112345005"),
                PhoneNumber.of("+491511700005"),
                PhoneNumber.of("+497119900005"),
                EmailAddress.of("matteo.rossi@company.com")
        ));
        insert(new EmployeeData(
                "Svenja",
                "Lina",
                "Krämer",
                "Svenja",
                LocalDate.of(1992, 2, 9),
                Gender.FEMALE,
                "Vegan",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Mittelstraße 12",
                        GermanPostalCode.of("40213"),
                        "Düsseldorf",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+49211345006"),
                PhoneNumber.of("+491511700006"),
                PhoneNumber.of("+492119900006"),
                EmailAddress.of("svenja.kraemer@company.com")
        ));
        insert(new EmployeeData(
                "Jonas",
                "Peter",
                "Becker",
                "Jonas",
                LocalDate.of(1997, 5, 2),
                Gender.MALE,
                "No pork",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Westenhellweg 9",
                        GermanPostalCode.of("44135"),
                        "Dortmund",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+492311234507"),
                PhoneNumber.of("+491511700007"),
                PhoneNumber.of("+492319900007"),
                EmailAddress.of("jonas.p.becker@company.com")
        ));
        insert(new EmployeeData(
                "Agnieszka",
                "Ewa",
                "Nowak",
                "Aga",
                LocalDate.of(1988, 8, 3),
                Gender.FEMALE,
                "Gluten-free",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Königstraße 20",
                        GermanPostalCode.of("90402"),
                        "Nürnberg",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+499112345008"),
                PhoneNumber.of("+491511700008"),
                PhoneNumber.of("+499119900008"),
                EmailAddress.of("agnieszka.nowak@company.com")
        ));
        insert(new EmployeeData(
                "Lukas",
                "Peter",
                "Vogel",
                "Lukas",
                LocalDate.of(1985, 10, 18),
                Gender.MALE,
                "None",
                ZoneId.of("Europe/Berlin"),
                new GermanPostalAddress(
                        "Grimmaische Straße 6",
                        GermanPostalCode.of("04109"),
                        "Leipzig",
                        Country.ofIsoCode("DE")
                ),
                PhoneNumber.of("+493412345009"),
                PhoneNumber.of("+491511700009"),
                PhoneNumber.of("+493419900009"),
                EmailAddress.of("lukas.vogel@company.com")
        ));

        // Canada
        insert(new EmployeeData(
                "Lucas",
                "Daniel",
                "Moreau",
                "Luke",
                LocalDate.of(1992, 9, 7),
                Gender.MALE,
                "None",
                ZoneId.of("America/Toronto"),
                new CanadianPostalAddress(
                        "55 York Street, Floor 12",
                        "Toronto",
                        CanadianProvince.ON,
                        CanadianPostalCode.of("M5J 1R7"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+14167000002"),
                PhoneNumber.of("+16477000002"),
                PhoneNumber.of("+14169000002"),
                EmailAddress.of("lucas.moreau@company.com")
        ));
        insert(new EmployeeData(
                "Sophie",
                "Marie",
                "Lefebvre",
                "Sophie",
                LocalDate.of(1990, 12, 1),
                Gender.FEMALE,
                "Gluten-free",
                ZoneId.of("America/Toronto"),
                new CanadianPostalAddress(
                        "50 O'Connor Street, Suite 900",
                        "Ottawa",
                        CanadianProvince.ON,
                        CanadianPostalCode.of("K1P 6L2"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+16138000003"),
                PhoneNumber.of("+16138000093"),
                PhoneNumber.of("+16137200003"),
                EmailAddress.of("sophie.lefebvre@company.com")
        ));
        insert(new EmployeeData(
                "Noah",
                "James",
                "Singh",
                "Noah",
                LocalDate.of(1985, 5, 14),
                Gender.MALE,
                "Peanut allergy",
                ZoneId.of("America/Toronto"),
                new CanadianPostalAddress(
                        "340 Albert Street, Suite 1200",
                        "Ottawa",
                        CanadianProvince.ON,
                        CanadianPostalCode.of("K1R 7Y6"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+16138000004"),
                PhoneNumber.of("+16138000094"),
                PhoneNumber.of("+16137200004"),
                EmailAddress.of("noah.singh@company.com")
        ));
        insert(new EmployeeData(
                "Clara",
                "Isabelle",
                "Gagnon",
                "Clara",
                LocalDate.of(1994, 4, 23),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("America/Toronto"),
                new CanadianPostalAddress(
                        "1250 René-Lévesque Blvd W, Suite 2000",
                        "Montreal",
                        CanadianProvince.QC,
                        CanadianPostalCode.of("H3B 4W8"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+15148000005"),
                PhoneNumber.of("+15149000005"),
                PhoneNumber.of("+15144200005"),
                EmailAddress.of("clara.gagnon@company.com")
        ));
        insert(new EmployeeData(
                "Mateo",
                "Andrés",
                "Rodriguez",
                "Mateo",
                LocalDate.of(1987, 7, 9),
                Gender.MALE,
                "Halal meals preferred",
                ZoneId.of("America/Toronto"),
                new CanadianPostalAddress(
                        "545 Rue Saint-Laurent, 4th Floor",
                        "Montreal",
                        CanadianProvince.QC,
                        CanadianPostalCode.of("H2Y 2Y9"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+15148000006"),
                PhoneNumber.of("+15149000006"),
                PhoneNumber.of("+15144200006"),
                EmailAddress.of("mateo.rodriguez@company.com")
        ));
        insert(new EmployeeData(
                "Emma",
                "Rose",
                "MacDonald",
                "Emma",
                LocalDate.of(1993, 1, 30),
                Gender.FEMALE,
                "Pescatarian",
                ZoneId.of("America/Edmonton"),
                new CanadianPostalAddress(
                        "333 7 Avenue SW, Suite 2100",
                        "Calgary",
                        CanadianProvince.AB,
                        CanadianPostalCode.of("T2P 2Z1"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+14038000007"),
                PhoneNumber.of("+15878000007"),
                PhoneNumber.of("+14032900007"),
                EmailAddress.of("emma.macdonald@company.com")
        ));
        insert(new EmployeeData(
                "Arjun",
                "Vikram",
                "Patel",
                "Arjun",
                LocalDate.of(1986, 10, 11),
                Gender.MALE,
                "None",
                ZoneId.of("America/Edmonton"),
                new CanadianPostalAddress(
                        "707 5 Street SW, Suite 1800",
                        "Calgary",
                        CanadianProvince.AB,
                        CanadianPostalCode.of("T2P 0Y3"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+14038000008"),
                PhoneNumber.of("+15878000008"),
                PhoneNumber.of("+14032900008"),
                EmailAddress.of("arjun.patel@company.com")
        ));
        insert(new EmployeeData(
                "Michelle",
                "Anne",
                "Wong",
                "Michelle",
                LocalDate.of(1989, 2, 5),
                Gender.FEMALE,
                "Vegetarian, no eggs",
                ZoneId.of("America/Vancouver"),
                new CanadianPostalAddress(
                        "1055 West Georgia Street, Suite 2400",
                        "Vancouver",
                        CanadianProvince.BC,
                        CanadianPostalCode.of("V6E 3P3"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+16048000009"),
                PhoneNumber.of("+17788000009"),
                PhoneNumber.of("+16046200009"),
                EmailAddress.of("michelle.wong@company.com")
        ));
        insert(new EmployeeData(
                "Kai",
                "River",
                "Thompson",
                "Kai",
                LocalDate.of(1996, 6, 17),
                Gender.OTHER,
                "None",
                ZoneId.of("America/Vancouver"),
                new CanadianPostalAddress(
                        "401 West Georgia Street, Suite 900",
                        "Vancouver",
                        CanadianProvince.BC,
                        CanadianPostalCode.of("V6B 5A1"),
                        Country.ofIsoCode("CA")
                ),
                PhoneNumber.of("+16048000010"),
                PhoneNumber.of("+17788000010"),
                PhoneNumber.of("+16046200010"),
                EmailAddress.of("kai.thompson@company.com")
        ));

        // United States
        insert(new EmployeeData(
                "Ethan",
                "James",
                "Park",
                "Ethan",
                LocalDate.of(1987, 3, 11),
                Gender.MALE,
                "None",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "525 Market Street, Suite 3200",
                        "San Francisco",
                        USState.CA,
                        USZipCode.of("94105"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+14155000001"),
                PhoneNumber.of("+14155090001"),
                PhoneNumber.of("+14155880001"),
                EmailAddress.of("ethan.park@company.com")
        ));
        insert(new EmployeeData(
                "Maya",
                "Arielle",
                "Sanchez",
                "Maya",
                LocalDate.of(1992, 9, 5),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "101 California Street, Floor 12",
                        "San Francisco",
                        USState.CA,
                        USZipCode.of("94111"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+14155000002"),
                PhoneNumber.of("+14155090002"),
                PhoneNumber.of("+14155880002"),
                EmailAddress.of("maya.sanchez@company.com")
        ));
        insert(new EmployeeData(
                "Noah",
                "Alexander",
                "Nguyen",
                "Noah",
                LocalDate.of(1986, 12, 2),
                Gender.MALE,
                "Gluten-free",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "1201 3rd Avenue",
                        "Seattle",
                        USState.WA,
                        USZipCode.of("98101"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12065000003"),
                PhoneNumber.of("+12065090003"),
                PhoneNumber.of("+12065880003"),
                EmailAddress.of("noah.nguyen@company.com")
        ));
        insert(new EmployeeData(
                "Harper",
                "Skye",
                "Bennett",
                "Harper",
                LocalDate.of(1995, 6, 18),
                Gender.OTHER,
                "Vegan",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "915 2nd Avenue",
                        "Seattle",
                        USState.WA,
                        USZipCode.of("98104"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12065000004"),
                PhoneNumber.of("+12065090004"),
                PhoneNumber.of("+12065880004"),
                EmailAddress.of("harper.bennett@company.com")
        ));
        insert(new EmployeeData(
                "Aaliyah",
                "Marie",
                "Johnson",
                "Aaliyah",
                LocalDate.of(1993, 4, 27),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "633 W 5th Street",
                        "Los Angeles",
                        USState.CA,
                        USZipCode.of("90071"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12135000005"),
                PhoneNumber.of("+13235090005"),
                PhoneNumber.of("+12135880005"),
                EmailAddress.of("aaliyah.johnson@company.com")
        ));
        insert(new EmployeeData(
                "Diego",
                "Luis",
                "Martinez",
                "Diego",
                LocalDate.of(1984, 1, 9),
                Gender.MALE,
                "Pescatarian",
                ZoneId.of("America/Los_Angeles"),
                new USPostalAddress(
                        "333 S Grand Avenue",
                        "Los Angeles",
                        USState.CA,
                        USZipCode.of("90071"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12135000006"),
                PhoneNumber.of("+13235090006"),
                PhoneNumber.of("+12135880006"),
                EmailAddress.of("diego.martinez@company.com")
        ));
        insert(new EmployeeData(
                "Olivia",
                "Grace",
                "Lee",
                "Olivia",
                LocalDate.of(1990, 7, 20),
                Gender.FEMALE,
                "Kosher-style, no pork",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "350 5th Avenue",
                        "New York",
                        USState.NY,
                        USZipCode.of("10118"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12125000007"),
                PhoneNumber.of("+16465090007"),
                PhoneNumber.of("+12125880007"),
                EmailAddress.of("olivia.lee@company.com")
        ));
        insert(new EmployeeData(
                "Liam",
                "Thomas",
                "O'Connor",
                "Liam",
                LocalDate.of(1988, 10, 14),
                Gender.MALE,
                "None",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "200 Park Avenue",
                        "New York",
                        USState.NY,
                        USZipCode.of("10166"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+12125000008"),
                PhoneNumber.of("+16465090008"),
                PhoneNumber.of("+12125880008"),
                EmailAddress.of("liam.oconnor@company.com")
        ));
        insert(new EmployeeData(
                "Emma",
                "Rose",
                "Murphy",
                "Emma",
                LocalDate.of(1994, 2, 1),
                Gender.FEMALE,
                "None",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "200 Clarendon Street",
                        "Boston",
                        USState.MA,
                        USZipCode.of("02116"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+16175000009"),
                PhoneNumber.of("+18575090009"),
                PhoneNumber.of("+16175880009"),
                EmailAddress.of("emma.murphy@company.com")
        ));
        insert(new EmployeeData(
                "Carter",
                "James",
                "Patel",
                "Carter",
                LocalDate.of(1986, 8, 23),
                Gender.MALE,
                "Peanut allergy",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "1 International Place",
                        "Boston",
                        USState.MA,
                        USZipCode.of("02110"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+16175000010"),
                PhoneNumber.of("+18575090010"),
                PhoneNumber.of("+16175880010"),
                EmailAddress.of("carter.patel@company.com")
        ));
        insert(new EmployeeData(
                "Isabella",
                "Sofia",
                "Rodriguez",
                "Isa",
                LocalDate.of(1995, 5, 6),
                Gender.FEMALE,
                "Halal meals preferred",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "701 Brickell Avenue",
                        "Miami",
                        USState.FL,
                        USZipCode.of("33131"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13055000011"),
                PhoneNumber.of("+17865090011"),
                PhoneNumber.of("+13055880011"),
                EmailAddress.of("isabella.rodriguez@company.com")
        ));
        insert(new EmployeeData(
                "Mateo",
                "Andres",
                "Gonzalez",
                "Mateo",
                LocalDate.of(1987, 11, 3),
                Gender.MALE,
                "None",
                ZoneId.of("America/New_York"),
                new USPostalAddress(
                        "1450 Brickell Avenue",
                        "Miami",
                        USState.FL,
                        USZipCode.of("33131"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13055000012"),
                PhoneNumber.of("+17865090012"),
                PhoneNumber.of("+13055880012"),
                EmailAddress.of("mateo.gonzalez@company.com")
        ));
        insert(new EmployeeData(
                "Sophia",
                "Anne",
                "Kowalski",
                "Sophia",
                LocalDate.of(1991, 3, 29),
                Gender.FEMALE,
                "Vegan, no soy",
                ZoneId.of("America/Chicago"),
                new USPostalAddress(
                        "233 S Wacker Drive",
                        "Chicago",
                        USState.IL,
                        USZipCode.of("60606"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13125000013"),
                PhoneNumber.of("+17735090013"),
                PhoneNumber.of("+13125880013"),
                EmailAddress.of("sophia.kowalski@company.com")
        ));
        insert(new EmployeeData(
                "Jackson",
                "Michael",
                "Reed",
                "Jack",
                LocalDate.of(1985, 9, 12),
                Gender.MALE,
                "None",
                ZoneId.of("America/Chicago"),
                new USPostalAddress(
                        "1 N State Street",
                        "Chicago",
                        USState.IL,
                        USZipCode.of("60602"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13125000014"),
                PhoneNumber.of("+17735090014"),
                PhoneNumber.of("+13125880014"),
                EmailAddress.of("jackson.reed@company.com")
        ));
        insert(new EmployeeData(
                "Asha",
                "Priya",
                "Desai",
                "Asha",
                LocalDate.of(1993, 7, 17),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("America/Chicago"),
                new USPostalAddress(
                        "600 Congress Avenue",
                        "Austin",
                        USState.TX,
                        USZipCode.of("78701"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+15125000015"),
                PhoneNumber.of("+17375090015"),
                PhoneNumber.of("+15125880015"),
                EmailAddress.of("asha.desai@company.com")
        ));
        insert(new EmployeeData(
                "Wyatt",
                "Cole",
                "Henderson",
                "Wyatt",
                LocalDate.of(1988, 2, 26),
                Gender.MALE,
                "None",
                ZoneId.of("America/Chicago"),
                new USPostalAddress(
                        "500 W 2nd Street",
                        "Austin",
                        USState.TX,
                        USZipCode.of("78701"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+15125000016"),
                PhoneNumber.of("+17375090016"),
                PhoneNumber.of("+15125880016"),
                EmailAddress.of("wyatt.henderson@company.com")
        ));
        insert(new EmployeeData(
                "Aiden",
                "Christopher",
                "Miller",
                "Aiden",
                LocalDate.of(1992, 6, 8),
                Gender.MALE,
                "Pescatarian",
                ZoneId.of("America/Denver"),
                new USPostalAddress(
                        "1700 Lincoln Street",
                        "Denver",
                        USState.CO,
                        USZipCode.of("80203"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13035000017"),
                PhoneNumber.of("+17205090017"),
                PhoneNumber.of("+13035880017"),
                EmailAddress.of("aiden.miller@company.com")
        ));
        insert(new EmployeeData(
                "Zoe",
                "Elizabeth",
                "Garcia",
                "Zoe",
                LocalDate.of(1996, 1, 31),
                Gender.OTHER,
                "Gluten-free",
                ZoneId.of("America/Denver"),
                new USPostalAddress(
                        "999 18th Street",
                        "Denver",
                        USState.CO,
                        USZipCode.of("80202"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+13035000018"),
                PhoneNumber.of("+17205090018"),
                PhoneNumber.of("+13035880018"),
                EmailAddress.of("zoe.garcia@company.com")
        ));
        insert(new EmployeeData(
                "Kai",
                "River",
                "Thompson",
                "Kai",
                LocalDate.of(1997, 5, 15),
                Gender.OTHER,
                "None",
                ZoneId.of("America/Phoenix"),
                new USPostalAddress(
                        "201 E Washington Street",
                        "Phoenix",
                        USState.AZ,
                        USZipCode.of("85004"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+16025000019"),
                PhoneNumber.of("+14805090019"),
                PhoneNumber.of("+16025880019"),
                EmailAddress.of("kai.thompson.us@company.com")
        ));
        insert(new EmployeeData(
                "Daniela",
                "María",
                "Hernandez",
                "Dani",
                LocalDate.of(1989, 9, 24),
                Gender.FEMALE,
                "No pork",
                ZoneId.of("America/Phoenix"),
                new USPostalAddress(
                        "1 N Central Avenue",
                        "Phoenix",
                        USState.AZ,
                        USZipCode.of("85004"),
                        Country.ofIsoCode("US")
                ),
                PhoneNumber.of("+16025000020"),
                PhoneNumber.of("+14805090020"),
                PhoneNumber.of("+16025880020"),
                EmailAddress.of("daniela.hernandez@company.com")
        ));

        // Argentina
        insert(new EmployeeData(
                "María",
                "Soledad",
                "Fernández",
                "Sol",
                LocalDate.of(1992, 7, 15),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("America/Argentina/Buenos_Aires"),
                new InternationalPostalAddress(
                        "Avenida Corrientes 1234, Piso 8, Departamento B",
                        "Buenos Aires",
                        "Buenos Aires",
                        "C1043AAZ",
                        Country.ofIsoCode("AR")
                ),
                PhoneNumber.of("+541143219876"),
                PhoneNumber.of("+5491165432109"),
                PhoneNumber.of("+541147654321"),
                EmailAddress.of("maria.fernandez@company.com")
        ));
        insert(new EmployeeData(
                "Santiago",
                "Martín",
                "Rodríguez",
                "Santi",
                LocalDate.of(1988, 11, 23),
                Gender.MALE,
                null,
                ZoneId.of("America/Argentina/Cordoba"),
                new InternationalPostalAddress(
                        "San Martín 567, 2° Piso",
                        "Córdoba",
                        "Córdoba",
                        "X5000KQN",
                        Country.ofIsoCode("AR")
                ),
                PhoneNumber.of("+543514876543"),
                PhoneNumber.of("+5493515123456"),
                PhoneNumber.of("+543514234567"),
                EmailAddress.of("santiago.rodriguez@company.com")
        ));

        // Bolivia
        insert(new EmployeeData(
                "Carlos",
                "Eduardo",
                "Mamani",
                "Carlos",
                LocalDate.of(1990, 4, 12),
                Gender.MALE,
                null,
                ZoneId.of("America/La_Paz"),
                new InternationalPostalAddress(
                        "Avenida 6 de Agosto 2345, Zona San Miguel",
                        "La Paz",
                        null,
                        null,
                        Country.ofIsoCode("BO")
                ),
                PhoneNumber.of("+59122765432"),
                PhoneNumber.of("+59171234567"),
                PhoneNumber.of("+59122123456"),
                EmailAddress.of("carlos.mamani@company.com")
        ));
        insert(new EmployeeData(
                "Andrea",
                "Paola",
                "Quispe",
                "Andi",
                LocalDate.of(1994, 9, 8),
                Gender.FEMALE,
                "No pork",
                ZoneId.of("America/La_Paz"),
                new InternationalPostalAddress(
                        "Calle Libertad 789, Zona Central",
                        "Santa Cruz de la Sierra",
                        null,
                        null,
                        Country.ofIsoCode("BO")
                ),
                PhoneNumber.of("+59133456789"),
                PhoneNumber.of("+59176543210"),
                PhoneNumber.of("+59133234567"),
                EmailAddress.of("andrea.quispe@company.com")
        ));

        // Brazil
        insert(new EmployeeData(
                "Lucas",
                "Henrique",
                "Silva",
                "Lucas",
                LocalDate.of(1991, 6, 20),
                Gender.MALE,
                null,
                ZoneId.of("America/Sao_Paulo"),
                new InternationalPostalAddress(
                        "Rua Augusta 1500, Apartamento 302",
                        "São Paulo",
                        "SP",
                        "01304-001",
                        Country.ofIsoCode("BR")
                ),
                PhoneNumber.of("+551132345678"),
                PhoneNumber.of("+5511987654321"),
                PhoneNumber.of("+551131234567"),
                EmailAddress.of("lucas.silva@company.com")
        ));
        insert(new EmployeeData(
                "Juliana",
                "Cristina",
                "Santos",
                "Ju",
                LocalDate.of(1987, 2, 14),
                Gender.FEMALE,
                "Lactose intolerant",
                ZoneId.of("America/Sao_Paulo"),
                new InternationalPostalAddress(
                        "Avenida Paulista 2000, Conjunto 1504",
                        "Rio de Janeiro",
                        "RJ",
                        "22071-000",
                        Country.ofIsoCode("BR")
                ),
                PhoneNumber.of("+552121234567"),
                PhoneNumber.of("+5521998765432"),
                PhoneNumber.of("+552122345678"),
                EmailAddress.of("juliana.santos@company.com")
        ));

        // Chile
        insert(new EmployeeData(
                "Alejandro",
                "José",
                "González",
                "Ale",
                LocalDate.of(1993, 10, 5),
                Gender.MALE,
                "Vegan",
                ZoneId.of("America/Santiago"),
                new InternationalPostalAddress(
                        "Avenida Providencia 1234, Oficina 901",
                        "Santiago",
                        "Región Metropolitana",
                        "7500000",
                        Country.ofIsoCode("CL")
                ),
                PhoneNumber.of("+56223456789"),
                PhoneNumber.of("+56987654321"),
                PhoneNumber.of("+56221234567"),
                EmailAddress.of("alejandro.gonzalez@company.com")
        ));
        insert(new EmployeeData(
                "Valentina",
                "Isabel",
                "Muñoz",
                "Vale",
                LocalDate.of(1995, 1, 18),
                Gender.FEMALE,
                null,
                ZoneId.of("America/Santiago"),
                new InternationalPostalAddress(
                        "Calle Merced 567, Departamento 405",
                        "Valparaíso",
                        "Región de Valparaíso",
                        "2340000",
                        Country.ofIsoCode("CL")
                ),
                PhoneNumber.of("+56322345678"),
                PhoneNumber.of("+56976543210"),
                PhoneNumber.of("+56321234567"),
                EmailAddress.of("valentina.munoz@company.com")
        ));

        // Colombia
        insert(new EmployeeData(
                "Andrés",
                "Felipe",
                "Ramírez",
                "Andrés",
                LocalDate.of(1989, 8, 30),
                Gender.MALE,
                null,
                ZoneId.of("America/Bogota"),
                new InternationalPostalAddress(
                        "Carrera 7 No. 71-21, Torre B, Oficina 1205",
                        "Bogotá",
                        "Cundinamarca",
                        "110231",
                        Country.ofIsoCode("CO")
                ),
                PhoneNumber.of("+5716012345"),
                PhoneNumber.of("+573201234567"),
                PhoneNumber.of("+5716011234"),
                EmailAddress.of("andres.ramirez@company.com")
        ));
        insert(new EmployeeData(
                "Camila",
                "Andrea",
                "Vargas",
                "Cami",
                LocalDate.of(1992, 12, 9),
                Gender.FEMALE,
                "Gluten-free",
                ZoneId.of("America/Bogota"),
                new InternationalPostalAddress(
                        "Calle 10 No. 5-61, Apartamento 302",
                        "Medellín",
                        "Antioquia",
                        "050021",
                        Country.ofIsoCode("CO")
                ),
                PhoneNumber.of("+5744123456"),
                PhoneNumber.of("+573109876543"),
                PhoneNumber.of("+5744234567"),
                EmailAddress.of("camila.vargas@company.com")
        ));

        // Ecuador
        insert(new EmployeeData(
                "Diego",
                "Alejandro",
                "Morales",
                "Diego",
                LocalDate.of(1990, 5, 25),
                Gender.MALE,
                null,
                ZoneId.of("America/Guayaquil"),
                new InternationalPostalAddress(
                        "Avenida Amazonas N24-123 y Colón, Edificio España, Piso 6",
                        "Quito",
                        "Pichincha",
                        "170143",
                        Country.ofIsoCode("EC")
                ),
                PhoneNumber.of("+59322345678"),
                PhoneNumber.of("+593987654321"),
                PhoneNumber.of("+59322123456"),
                EmailAddress.of("diego.morales@company.com")
        ));
        insert(new EmployeeData(
                "Gabriela",
                "Sofía",
                "Jaramillo",
                "Gaby",
                LocalDate.of(1994, 3, 17),
                Gender.FEMALE,
                "Pescatarian",
                ZoneId.of("America/Guayaquil"),
                new InternationalPostalAddress(
                        "Avenida 9 de Octubre 456, Oficina 803",
                        "Guayaquil",
                        "Guayas",
                        "090313",
                        Country.ofIsoCode("EC")
                ),
                PhoneNumber.of("+59342345678"),
                PhoneNumber.of("+593976543210"),
                PhoneNumber.of("+59342234567"),
                EmailAddress.of("gabriela.jaramillo@company.com")
        ));

        // Guyana
        insert(new EmployeeData(
                "Ryan",
                "Christopher",
                "Singh",
                "Ryan",
                LocalDate.of(1991, 7, 11),
                Gender.MALE,
                "No beef",
                ZoneId.of("America/Guyana"),
                new InternationalPostalAddress(
                        "123 Main Street, Queenstown",
                        "Georgetown",
                        null,
                        null,
                        Country.ofIsoCode("GY")
                ),
                PhoneNumber.of("+5922225678"),
                PhoneNumber.of("+5926123456"),
                PhoneNumber.of("+5922234567"),
                EmailAddress.of("ryan.singh@company.com")
        ));
        insert(new EmployeeData(
                "Priya",
                "Michelle",
                "Persaud",
                "Priya",
                LocalDate.of(1993, 11, 28),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("America/Guyana"),
                new InternationalPostalAddress(
                        "67 Robb Street, Bourda",
                        "Georgetown",
                        null,
                        null,
                        Country.ofIsoCode("GY")
                ),
                PhoneNumber.of("+5922267890"),
                PhoneNumber.of("+5926987654"),
                PhoneNumber.of("+5922223456"),
                EmailAddress.of("priya.persaud@company.com")
        ));

        // Paraguay
        insert(new EmployeeData(
                "Miguel",
                "Ángel",
                "Benítez",
                "Miguel",
                LocalDate.of(1988, 4, 22),
                Gender.MALE,
                null,
                ZoneId.of("America/Asuncion"),
                new InternationalPostalAddress(
                        "Avenida Mariscal López 1234, Edificio San Rafael, Piso 5",
                        "Asunción",
                        null,
                        "1209",
                        Country.ofIsoCode("PY")
                ),
                PhoneNumber.of("+595212345678"),
                PhoneNumber.of("+595981234567"),
                PhoneNumber.of("+595211234567"),
                EmailAddress.of("miguel.benitez@company.com")
        ));
        insert(new EmployeeData(
                "Laura",
                "Beatriz",
                "Cardozo",
                "Lau",
                LocalDate.of(1996, 9, 3),
                Gender.FEMALE,
                "Dairy-free",
                ZoneId.of("America/Asuncion"),
                new InternationalPostalAddress(
                        "Calle Palma 567, Villa Morra",
                        "Asunción",
                        null,
                        "1536",
                        Country.ofIsoCode("PY")
                ),
                PhoneNumber.of("+595213456789"),
                PhoneNumber.of("+595971234567"),
                PhoneNumber.of("+595212345678"),
                EmailAddress.of("laura.cardozo@company.com")
        ));

        // Peru
        insert(new EmployeeData(
                "José",
                "Luis",
                "Flores",
                "Pepe",
                LocalDate.of(1990, 2, 16),
                Gender.MALE,
                null,
                ZoneId.of("America/Lima"),
                new InternationalPostalAddress(
                        "Avenida Arequipa 1234, Oficina 601, Santa Beatriz",
                        "Lima",
                        "Lima",
                        "15046",
                        Country.ofIsoCode("PE")
                ),
                PhoneNumber.of("+5114567890"),
                PhoneNumber.of("+51987654321"),
                PhoneNumber.of("+5114123456"),
                EmailAddress.of("jose.flores@company.com")
        ));
        insert(new EmployeeData(
                "Ana",
                "María",
                "Castillo",
                "Anita",
                LocalDate.of(1992, 8, 7),
                Gender.FEMALE,
                "Halal meals only",
                ZoneId.of("America/Lima"),
                new InternationalPostalAddress(
                        "Jirón de la Unión 890, Centro Histórico",
                        "Cusco",
                        "Cusco",
                        "08002",
                        Country.ofIsoCode("PE")
                ),
                PhoneNumber.of("+51842345678"),
                PhoneNumber.of("+51976543210"),
                PhoneNumber.of("+51841234567"),
                EmailAddress.of("ana.castillo@company.com")
        ));

        // Uruguay
        insert(new EmployeeData(
                "Martín",
                "Sebastián",
                "Pérez",
                "Martín",
                LocalDate.of(1989, 6, 13),
                Gender.MALE,
                null,
                ZoneId.of("America/Montevideo"),
                new InternationalPostalAddress(
                        "Avenida 18 de Julio 1234, Piso 8, Oficina 802",
                        "Montevideo",
                        null,
                        "11100",
                        Country.ofIsoCode("UY")
                ),
                PhoneNumber.of("+59822345678"),
                PhoneNumber.of("+59894123456"),
                PhoneNumber.of("+59822234567"),
                EmailAddress.of("martin.perez@company.com")
        ));
        insert(new EmployeeData(
                "Sofía",
                "Valentina",
                "García",
                "Sofi",
                LocalDate.of(1995, 12, 1),
                Gender.FEMALE,
                "Vegetarian",
                ZoneId.of("America/Montevideo"),
                new InternationalPostalAddress(
                        "Rambla República de México 5678, Punta Carretas",
                        "Montevideo",
                        null,
                        "11400",
                        Country.ofIsoCode("UY")
                ),
                PhoneNumber.of("+59827123456"),
                PhoneNumber.of("+59899876543"),
                PhoneNumber.of("+59826234567"),
                EmailAddress.of("sofia.garcia@company.com")
        ));

        // Venezuela
        insert(new EmployeeData(
                "Rafael",
                "Antonio",
                "Méndez",
                "Rafa",
                LocalDate.of(1987, 10, 24),
                Gender.MALE,
                null,
                ZoneId.of("America/Caracas"),
                new InternationalPostalAddress(
                        "Avenida Francisco de Miranda, Centro Lido, Torre A, Piso 12",
                        "Caracas",
                        "Distrito Capital",
                        "1060",
                        Country.ofIsoCode("VE")
                ),
                PhoneNumber.of("+582122345678"),
                PhoneNumber.of("+584141234567"),
                PhoneNumber.of("+582121234567"),
                EmailAddress.of("rafael.mendez@company.com")
        ));
        insert(new EmployeeData(
                "Isabella",
                "Carolina",
                "Rivas",
                "Isa",
                LocalDate.of(1994, 5, 19),
                Gender.FEMALE,
                "No shellfish",
                ZoneId.of("America/Caracas"),
                new InternationalPostalAddress(
                        "Avenida 5 de Julio con Calle 72, Edificio Empresarial, Oficina 503",
                        "Maracaibo",
                        "Zulia",
                        "4001",
                        Country.ofIsoCode("VE")
                ),
                PhoneNumber.of("+582612345678"),
                PhoneNumber.of("+584246543210"),
                PhoneNumber.of("+582611234567"),
                EmailAddress.of("isabella.rivas@company.com")
        ));
    }

    private static final String[] JOB_TITLES = {
            "Software Engineer",
            "Product Manager",
            "Data Analyst",
            "UX Designer",
            "Marketing Manager",
            "Content Writer",
            "Financial Analyst",
            "HR Manager",
            "Project Manager",
            "Business Analyst",
            "DevOps Engineer",
            "Sales Representative",
            "Graphic Designer",
            "Accountant",
            "Legal Counsel",
            "Customer Success Manager",
            "Technical Writer",
            "Recruiter",
            "Social Media Manager",
            "Operations Manager"
    };

    private EmploymentStatus pickEmploymentStatus() {
        var n = rnd.nextInt(100);
        if (n < 5) {
            return EmploymentStatus.TERMINATED;
        } else if (n < 10) {
            return EmploymentStatus.INACTIVE;
        } else {
            return EmploymentStatus.ACTIVE;
        }
    }

    private void insert(EmployeeData employeeData) {
        var id = employeeRepository.insert(employeeData);
        var employmentStatus = pickEmploymentStatus();
        var hireDate = LocalDate.of(2010, 1, 1).plusDays(rnd.nextLong(5000));
        var terminationDate = generateTerminationDate(employmentStatus, hireDate);
        var employmentDetails = new EmploymentDetailsData(
                pickRandom(JOB_TITLES),
                pickRandom(EmploymentType.values()),
                employmentStatus,
                pickRandom(WorkArrangement.values()),
                pickRandom(locationTestDataService.getLocationsOfCountry(employeeData.homeAddress().country())),
                null, // TODO Pick manager
                hireDate,
                terminationDate
        );
        employmentDetailsRepository.insert(id, employmentDetails);
    }

    private @Nullable LocalDate generateTerminationDate(EmploymentStatus employmentStatus, LocalDate hireDate) {
        if (employmentStatus != EmploymentStatus.TERMINATED) {
            return null;
        }
        var date = hireDate.plusMonths(rnd.nextInt(47) + 1);
        if (date.isAfter(LocalDate.now())) {
            return LocalDate.now();
        } else {
            return date;
        }
    }
}
