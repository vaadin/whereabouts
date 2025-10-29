package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.address.*;
import com.example.whereabouts.humanresources.repository.LocationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Component
public class LocationTestDataService {

    private final Random rnd = new Random();
    private final LocationRepository locationRepository;
    private final Map<Country, List<LocationId>> locationsByCountry = new HashMap<>();

    LocationTestDataService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    private List<LocationFacility> generateRandomFacilities() {
        List<LocationFacility> facilities = new ArrayList<>();
        // This can lead to some comic test data, like a huge remote hub or a small HQ. So be it.
        if (rnd.nextBoolean()) {
            facilities.add(new LocationFacility.AccessibleOffice());
        }
        if (rnd.nextBoolean()) {
            facilities.add(new LocationFacility.Kitchen());
        }
        facilities.add(new LocationFacility.FloorSpace(100 + rnd.nextInt(2901)));
        facilities.add(new LocationFacility.HotDesks(1 + rnd.nextInt(200)));
        facilities.add(new LocationFacility.MeetingBooths(1 + rnd.nextInt(20)));
        facilities.add(new LocationFacility.ParkingSlots(1 + rnd.nextInt(200)));
        return facilities;
    }

    public List<LocationId> getLocationsOfCountry(Country country) {
        return locationsByCountry.getOrDefault(country, Collections.emptyList());
    }

    @Transactional
    public void createTestLocations() {
        // Argentina locations
        locationsByCountry.put(Country.ofIsoCode("AR"), List.of(
                locationRepository.insert(new LocationData(
                        "Buenos Aires",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. Corrientes 456, Piso 10, Microcentro",
                                "Buenos Aires",
                                null,
                                "C1043AAR",
                                Country.ofIsoCode("AR")
                        ),
                        LocalDate.of(2020, 9, 3),
                        "Situated in the bustling Microcentro, this office embodies Buenos Aires' cosmopolitan energy. Features include tango-inspired collaborative spaces and a full-service cafeteria serving traditional mate.",
                        ZoneId.of("America/Argentina/Buenos_Aires"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Mendoza",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "San Martín 1234, Oficina 501, Centro",
                                "Mendoza",
                                null,
                                "M5500CCG",
                                Country.ofIsoCode("AR")
                        ),
                        LocalDate.of(2022, 4, 20),
                        "Nestled at the foot of the Andes, our Mendoza office offers stunning mountain views. The location emphasizes outdoor-inspired design with extensive natural lighting and eco-friendly materials.",
                        ZoneId.of("America/Argentina/Mendoza"),
                        generateRandomFacilities()
                ))
        ));

        // Bolivia location
        locationsByCountry.put(Country.ofIsoCode("BO"), List.of(
                locationRepository.insert(new LocationData(
                        "La Paz",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. 16 de Julio 1490, Edificio Alameda, Piso 11, El Prado",
                                "La Paz",
                                null,
                                "9999",
                                Country.ofIsoCode("BO")
                        ),
                        LocalDate.of(2023, 3, 22),
                        "Perched in the world's highest administrative capital, our La Paz office offers breathtaking views of the Altiplano. Specially designed for high-altitude work with oxygen-enriched environments.",
                        ZoneId.of("America/La_Paz"),
                        generateRandomFacilities()
                ))
        ));

        // Brazil locations
        locationsByCountry.put(Country.ofIsoCode("BR"), List.of(
                locationRepository.insert(new LocationData(
                        "São Paulo Centro",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Avenida Paulista 1578, 12º andar, Bela Vista",
                                "São Paulo",
                                null,
                                "01310-200",
                                Country.ofIsoCode("BR")
                        ),
                        LocalDate.of(2021, 3, 15),
                        "Located in the heart of São Paulo's financial district, this office combines modern architecture with Brazilian warmth. Features include collaborative spaces, a rooftop garden, and state-of-the-art meeting facilities.",
                        ZoneId.of("America/Sao_Paulo"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Rio de Janeiro",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Rua da Assembleia 92, 8º andar, Centro",
                                "Rio de Janeiro",
                                null,
                                "20011-000",
                                Country.ofIsoCode("BR")
                        ),
                        LocalDate.of(2020, 11, 8),
                        "Our Rio office captures the vibrant spirit of the city with panoramic views of Guanabara Bay. The space promotes creativity through flexible work zones and a focus on work-life balance.",
                        ZoneId.of("America/Sao_Paulo"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Brasília",
                        LocationType.REGIONAL_HQ,
                        new InternationalPostalAddress(
                                "SCS Quadra 2, Bloco C, Sala 301, Asa Sul",
                                "Brasília",
                                null,
                                "70302-000",
                                Country.ofIsoCode("BR")
                        ),
                        LocalDate.of(2019, 6, 12),
                        "Our Brazil headquarters in the capital reflects the city's modernist architecture. This flagship location serves as the central hub for operations across South America with cutting-edge technology infrastructure.",
                        ZoneId.of("America/Sao_Paulo"),
                        generateRandomFacilities()
                ))
        ));

        // Canada locations
        locationsByCountry.put(Country.ofIsoCode("CA"), List.of(
                locationRepository.insert(new LocationData(
                        "Toronto",
                        LocationType.BRANCH_OFFICE,
                        new CanadianPostalAddress(
                                "100 King Street West, Suite 5600",
                                "Toronto",
                                CanadianProvince.ON,
                                CanadianPostalCode.of("M5X 1C9"),
                                Country.ofIsoCode("CA")
                        ),
                        LocalDate.of(2019, 4, 11),
                        "Our Toronto office in the Financial District serves as our Canadian headquarters. The space features bilingual facilities, views of Lake Ontario and the CN Tower, and design elements celebrating Canada's multicultural identity.",
                        ZoneId.of("America/Toronto"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Ottawa",
                        LocationType.BRANCH_OFFICE,
                        new CanadianPostalAddress(
                                "50 O'Connor Street, Suite 1100",
                                "Ottawa",
                                CanadianProvince.ON,
                                CanadianPostalCode.of("K1P 6L2"),
                                Country.ofIsoCode("CA")
                        ),
                        LocalDate.of(2020, 9, 23),
                        "Located in the nation's capital near Parliament Hill, this office blends governmental proximity with tech sector innovation. Features include secure meeting facilities and a focus on public-private sector collaboration.",
                        ZoneId.of("America/Toronto"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Montreal",
                        LocationType.BRANCH_OFFICE,
                        new CanadianPostalAddress(
                                "1250 René-Lévesque Boulevard West, Suite 4200",
                                "Montreal",
                                CanadianProvince.QC,
                                CanadianPostalCode.of("H3B 4W8"),
                                Country.ofIsoCode("CA")
                        ),
                        LocalDate.of(2021, 1, 28),
                        "Our Montreal office embraces the city's European charm and bilingual culture. Located downtown with Mount Royal views, the space features French-English collaborative environments and celebrates Quebec's unique cultural heritage.",
                        ZoneId.of("America/Toronto"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Calgary",
                        LocationType.BRANCH_OFFICE,
                        new CanadianPostalAddress(
                                "350 7th Avenue SW, Suite 3900",
                                "Calgary",
                                CanadianProvince.AB,
                                CanadianPostalCode.of("T2P 3N9"),
                                Country.ofIsoCode("CA")
                        ),
                        LocalDate.of(2020, 11, 5),
                        "Situated in Calgary's energy sector hub with Rocky Mountain proximity, this office combines resource industry expertise with sustainable practices. Features include advanced HVAC for extreme winters and connections to the Plus 15 walkway system.",
                        ZoneId.of("America/Edmonton"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Vancouver",
                        LocationType.BRANCH_OFFICE,
                        new CanadianPostalAddress(
                                "1055 West Georgia Street, Suite 1500",
                                "Vancouver",
                                CanadianProvince.BC,
                                CanadianPostalCode.of("V6E 3P3"),
                                Country.ofIsoCode("CA")
                        ),
                        LocalDate.of(2019, 7, 19),
                        "Our Vancouver office offers stunning North Shore mountain and Pacific Ocean views. The space emphasizes West Coast sustainability, features extensive use of BC timber, and serves as our Asia-Pacific gateway with strong trade connections.",
                        ZoneId.of("America/Vancouver"),
                        generateRandomFacilities()
                ))
        ));

        // Chile locations
        locationsByCountry.put(Country.ofIsoCode("CL"), List.of(
                locationRepository.insert(new LocationData(
                        "Santiago",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. Apoquindo 3721, Piso 15, Las Condes",
                                "Santiago",
                                null,
                                "7550000",
                                Country.ofIsoCode("CL")
                        ),
                        LocalDate.of(2021, 7, 14),
                        "Our Santiago office in the prestigious Las Condes district combines Chilean hospitality with modern efficiency. The space features earthquake-resistant design and panoramic Andes views.",
                        ZoneId.of("America/Santiago"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Valparaíso",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Blanco 1199, Cerro Alegre",
                                "Valparaíso",
                                null,
                                "2340000",
                                Country.ofIsoCode("CL")
                        ),
                        LocalDate.of(2023, 2, 11),
                        "Located in the colorful UNESCO World Heritage port city, this office celebrates Valparaíso's artistic heritage. Features include restored historic architecture blended with contemporary workspaces.",
                        ZoneId.of("America/Santiago"),
                        generateRandomFacilities()
                ))
        ));

        // Colombia locations
        locationsByCountry.put(Country.ofIsoCode("CO"), List.of(
                locationRepository.insert(new LocationData(
                        "Bogotá",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Carrera 7 #71-52, Torre A, Piso 12, Chapinero",
                                "Bogotá",
                                null,
                                "110231",
                                Country.ofIsoCode("CO")
                        ),
                        LocalDate.of(2021, 5, 19),
                        "Situated in dynamic Chapinero, our Bogotá office reflects Colombia's innovative spirit. The high-altitude location features climate-controlled environments and spaces designed for collaboration and focus.",
                        ZoneId.of("America/Bogota"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Medellín",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Calle 7 Sur #42-70, Piso 8, El Poblado",
                                "Medellín",
                                null,
                                "050021",
                                Country.ofIsoCode("CO")
                        ),
                        LocalDate.of(2022, 8, 27),
                        "Our Medellín office captures the city's transformation story. Located in trendy El Poblado, it features innovative design inspired by the city's metro cable system and commitment to urban renewal.",
                        ZoneId.of("America/Bogota"),
                        generateRandomFacilities()
                ))
        ));

        // Ecuador location
        locationsByCountry.put(Country.ofIsoCode("EC"), List.of(
                locationRepository.insert(new LocationData(
                        "Quito",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. República de El Salvador N36-84, Edificio Prisma, Piso 7",
                                "Quito",
                                null,
                                "170515",
                                Country.ofIsoCode("EC")
                        ),
                        LocalDate.of(2022, 6, 30),
                        "Our Quito office sits at 2,850 metres above sea level in the financial district. Designed for the high altitude, it features enhanced ventilation systems and spaces that celebrate Ecuador's biodiversity.",
                        ZoneId.of("America/Guayaquil"),
                        generateRandomFacilities()
                ))
        ));

        // Finland locations
        locationsByCountry.put(Country.ofIsoCode("FI"), List.of(
                locationRepository.insert(new LocationData(
                        "Helsinki",
                        LocationType.GLOBAL_HQ,
                        new FinnishPostalAddress(
                                "Mannerheimintie 12 B",
                                FinnishPostalCode.of("00100"),
                                "Helsinki",
                                Country.ofIsoCode("FI")
                        ),
                        LocalDate.of(2016, 5, 3),
                        "Our global headquarters in the heart of Helsinki combines Nordic design principles with cutting-edge technology. The space features energy-efficient systems, extensive natural light management for dark winters, and promotes Finnish values of equality and innovation. Sauna facilities and locally-sourced materials reflect authentic Finnish workplace culture.",
                        ZoneId.of("Europe/Helsinki"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Espoo",
                        LocationType.REMOTE_HUB,
                        new FinnishPostalAddress(
                                "Keilaniementie 1",
                                FinnishPostalCode.of("02150"),
                                "Espoo",
                                Country.ofIsoCode("FI")
                        ),
                        LocalDate.of(2021, 8, 17),
                        "Located in Otaniemi tech hub near Aalto University, this remote hub serves as a collaboration space for distributed teams. Features include high-speed connectivity, flexible hot-desking arrangements, and proximity to Finland's startup ecosystem.",
                        ZoneId.of("Europe/Helsinki"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Turku",
                        LocationType.REMOTE_HUB,
                        new FinnishPostalAddress(
                                "Yliopistonkatu 29",
                                FinnishPostalCode.of("20100"),
                                "Turku",
                                Country.ofIsoCode("FI")
                        ),
                        LocalDate.of(2022, 2, 14),
                        "Our Turku remote hub in Finland's oldest city blends maritime heritage with modern remote work infrastructure. The historic building features restored architecture, collaborative spaces for hybrid teams, and excellent connections to the archipelago for work-life balance.",
                        ZoneId.of("Europe/Helsinki"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Tampere",
                        LocationType.REMOTE_HUB,
                        new FinnishPostalAddress(
                                "Hämeenkatu 21 A",
                                FinnishPostalCode.of("33200"),
                                "Tampere",
                                Country.ofIsoCode("FI")
                        ),
                        LocalDate.of(2021, 11, 9),
                        "Situated between two lakes in Finland's industrial capital, this remote hub celebrates Tampere's transformation into a knowledge economy center. Features include game industry-inspired collaborative spaces, excellent rail connections, and a focus on work-life integration.",
                        ZoneId.of("Europe/Helsinki"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Oulu",
                        LocationType.REMOTE_HUB,
                        new FinnishPostalAddress(
                                "Kauppurienkatu 23",
                                FinnishPostalCode.of("90100"),
                                "Oulu",
                                Country.ofIsoCode("FI")
                        ),
                        LocalDate.of(2022, 6, 28),
                        "Our northernmost remote hub in Oulu serves distributed teams across the Nordic region. Designed for extreme winter conditions with advanced heating systems, the space offers aurora viewing opportunities and embodies Finland's expertise in northern technology and remote collaboration.",
                        ZoneId.of("Europe/Helsinki"),
                        generateRandomFacilities()
                ))
        ));

        // German locations
        locationsByCountry.put(Country.ofIsoCode("DE"), List.of(
                locationRepository.insert(new LocationData(
                        "Berlin",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Friedrichstraße 95",
                                GermanPostalCode.of("10117"),
                                "Berlin",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2019, 3, 20),
                        "Our Berlin office in the historic Mitte district embodies the city's innovative startup culture and rich history. The space features restored pre-war architecture blended with modern design, excellent public transport connections, and collaborative areas that celebrate Berlin's creative energy.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Munich",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Maximilianstraße 13",
                                GermanPostalCode.of("80539"),
                                "München",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2018, 9, 12),
                        "Located in Munich's prestigious city center near the Alps, this office combines Bavarian tradition with high-tech innovation. Features include proximity to leading research institutions, advanced engineering workspaces, and a focus on precision and quality.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Frankfurt",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Bockenheimer Landstraße 2-4",
                                GermanPostalCode.of("60306"),
                                "Frankfurt am Main",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2017, 11, 8),
                        "Our Frankfurt office in the financial capital serves as the gateway to European markets. The modern high-rise location offers skyline views, state-of-the-art trading floor technology, and seamless connections to Frankfurt Airport for international business.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Hamburg",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Jungfernstieg 38",
                                GermanPostalCode.of("20354"),
                                "Hamburg",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2019, 6, 25),
                        "Situated on the Binnenalster in Germany's maritime capital, this office reflects Hamburg's trading heritage and port city character. The space features harbor views, a focus on logistics and international commerce, and Hanseatic design sensibility.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Cologne",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Tunisstraße 19-23",
                                GermanPostalCode.of("50667"),
                                "Köln",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2020, 4, 16),
                        "Our Cologne office near the iconic Dom cathedral combines Rhineland hospitality with media industry expertise. Located in the city's vibrant cultural quarter, the space emphasizes creative collaboration and digital media innovation.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Stuttgart",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Königstraße 10C3",
                                GermanPostalCode.of("70173"),
                                "Stuttgart",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2019, 10, 30),
                        "Located in the heart of Germany's automotive industry, our Stuttgart office celebrates engineering excellence. The space features advanced prototyping facilities, connections to major manufacturers, and design inspired by Swabian precision.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Düsseldorf",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Königsallee 92a",
                                GermanPostalCode.of("40212"),
                                "Düsseldorf",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2020, 7, 22),
                        "Our Düsseldorf office on the prestigious Kö serves international corporations and Japanese business connections. The elegant space reflects the city's role as a corporate headquarters hub with focus on Asian-European business relations.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Dortmund",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Kampstraße 45",
                                GermanPostalCode.of("44137"),
                                "Dortmund",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2021, 5, 13),
                        "Situated in the Ruhr Valley, our Dortmund office represents the region's transformation from industry to technology. The space features regenerated industrial architecture, strong logistics infrastructure, and celebrates the area's football culture.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Leipzig",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Petersstraße 32-34",
                                GermanPostalCode.of("04109"),
                                "Leipzig",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2021, 9, 8),
                        "Our Leipzig office in Saxony's cultural center blends East German heritage with modern startup energy. The space features creative industries focus, excellent university connections, and celebrates the city's musical and publishing traditions.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Nuremberg",
                        LocationType.BRANCH_OFFICE,
                        new GermanPostalAddress(
                                "Königstraße 21",
                                GermanPostalCode.of("90402"),
                                "Nürnberg",
                                Country.ofIsoCode("DE")
                        ),
                        LocalDate.of(2022, 3, 31),
                        "Located in Franconia's historic capital, our Nuremberg office combines medieval charm with modern technology sectors. The space features proximity to the Germanisches Nationalmuseum, strong toy and games industry connections, and traditional Franconian hospitality.",
                        ZoneId.of("Europe/Berlin"),
                        generateRandomFacilities()
                ))
        ));

        // Guyana location
        locationsByCountry.put(Country.ofIsoCode("GY"), List.of(
                locationRepository.insert(new LocationData(
                        "Georgetown",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Robb Street 45, Water Lily Building, 3rd Floor",
                                "Georgetown",
                                null,
                                "592",
                                Country.ofIsoCode("GY")
                        ),
                        LocalDate.of(2023, 9, 7),
                        "Our Georgetown office represents our expansion into the Guianas, featuring Caribbean-influenced architecture. The space embraces Guyana's multicultural identity and serves as a gateway to the region.",
                        ZoneId.of("America/Guyana"),
                        generateRandomFacilities()
                ))
        ));

        // Paraguay location
        locationsByCountry.put(Country.ofIsoCode("PY"), List.of(
                locationRepository.insert(new LocationData(
                        "Asunción",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. Mariscal López 3794, Piso 4, Villa Morra",
                                "Asunción",
                                null,
                                "1632",
                                Country.ofIsoCode("PY")
                        ),
                        LocalDate.of(2022, 11, 16),
                        "Our Asunción office in the Villa Morra business district embraces Paraguay's bilingual culture. The space features Guaraní-inspired design elements and promotes cross-cultural collaboration.",
                        ZoneId.of("America/Asuncion"),
                        generateRandomFacilities()
                ))
        ));

        // Peru locations
        locationsByCountry.put(Country.ofIsoCode("PE"), List.of(
                locationRepository.insert(new LocationData(
                        "Lima",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. José Larco 1301, Piso 9, Miraflores",
                                "Lima",
                                null,
                                "15074",
                                Country.ofIsoCode("PE")
                        ),
                        LocalDate.of(2020, 10, 5),
                        "Overlooking the Pacific Ocean in upscale Miraflores, this office blends Peruvian heritage with modern amenities. Features include a wellness room and spaces showcasing local artisan work.",
                        ZoneId.of("America/Lima"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Arequipa",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Calle Mercaderes 121, Oficina 302, Centro Histórico",
                                "Arequipa",
                                null,
                                "04001",
                                Country.ofIsoCode("PE")
                        ),
                        LocalDate.of(2023, 1, 18),
                        "Set against the backdrop of three volcanoes, our Arequipa office honors the White City's colonial architecture. The space features volcanic stone accents and earthquake-resistant construction.",
                        ZoneId.of("America/Lima"),
                        generateRandomFacilities()
                ))
        ));

        // United States location
        locationsByCountry.put(Country.ofIsoCode("US"), List.of(
                locationRepository.insert(new LocationData(
                        "San Francisco",
                        LocationType.REGIONAL_HQ,
                        new USPostalAddress(
                                "101 California Street, Suite 2800",
                                "San Francisco",
                                USState.CA,
                                USZipCode.of("94111"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2018, 3, 15),
                        "Our San Francisco headquarters in the Financial District serves as the innovation hub for North American operations. Features include a rooftop terrace with bay views, collaborative tech labs, and a commitment to sustainable urban workspace design.",
                        ZoneId.of("America/Los_Angeles"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Seattle",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "500 Pike Street, Floor 12",
                                "Seattle",
                                USState.WA,
                                USZipCode.of("98101"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2019, 6, 22),
                        "Located in downtown Seattle near Pike Place Market, this office captures the Pacific Northwest spirit. The space features extensive natural wood elements, mountain and sound views, and barista-quality coffee stations.",
                        ZoneId.of("America/Los_Angeles"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Los Angeles",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "633 West 5th Street, Suite 2600",
                                "Los Angeles",
                                USState.CA,
                                USZipCode.of("90071"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2020, 1, 10),
                        "Our LA office in the downtown financial district blends California innovation with entertainment industry creativity. Features include media production spaces, outdoor terraces, and a focus on health and wellness.",
                        ZoneId.of("America/Los_Angeles"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "New York",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "1271 Avenue of the Americas, 42nd Floor",
                                "New York",
                                USState.NY,
                                USZipCode.of("10020"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2017, 9, 8),
                        "Situated in Midtown Manhattan, our New York office embodies the city's fast-paced energy. The high-rise location offers iconic skyline views, state-of-the-art conference facilities, and 24/7 access for global collaboration.",
                        ZoneId.of("America/New_York"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Boston",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "225 Franklin Street, 26th Floor",
                                "Boston",
                                USState.MA,
                                USZipCode.of("02110"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2019, 11, 14),
                        "Our Boston office in the Financial District combines New England tradition with tech innovation. Located near leading universities, the space emphasizes research collaboration and features historic brick-and-beam architecture.",
                        ZoneId.of("America/New_York"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Miami",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "1450 Brickell Avenue, Suite 1900",
                                "Miami",
                                USState.FL,
                                USZipCode.of("33131"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2021, 2, 18),
                        "Located in Brickell financial district, our Miami office serves as the gateway to Latin America. The tropical modern design features hurricane-resistant construction, bilingual facilities, and panoramic bay views.",
                        ZoneId.of("America/New_York"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Chicago",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "233 South Wacker Drive, Suite 7800",
                                "Chicago",
                                USState.IL,
                                USZipCode.of("60606"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2018, 10, 25),
                        "Our Chicago office in the Willis Tower area captures Midwest work ethic and architectural excellence. Features include Lake Michigan views, winter-optimized climate control, and spaces celebrating Chicago's design legacy.",
                        ZoneId.of("America/Chicago"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Austin",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "98 San Jacinto Boulevard, Suite 1500",
                                "Austin",
                                USState.TX,
                                USZipCode.of("78701"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2020, 8, 6),
                        "Situated in downtown Austin, this office embraces the city's \"Keep Austin Weird\" culture and tech boom. The space features live music venue aesthetics, outdoor work areas, and a strong startup collaboration focus.",
                        ZoneId.of("America/Chicago"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Denver",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "1801 California Street, Suite 4400",
                                "Denver",
                                USState.CO,
                                USZipCode.of("80202"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2021, 5, 20),
                        "Our Denver office at 1,609 metres elevation offers Rocky Mountain views and outdoor-inspired design. The space features altitude-optimized climate systems, bike storage, and proximity to outdoor recreation.",
                        ZoneId.of("America/Denver"),
                        generateRandomFacilities()
                )),
                locationRepository.insert(new LocationData(
                        "Phoenix",
                        LocationType.BRANCH_OFFICE,
                        new USPostalAddress(
                                "2398 East Camelback Road, Suite 300",
                                "Phoenix",
                                USState.AZ,
                                USZipCode.of("85016"),
                                Country.ofIsoCode("US")
                        ),
                        LocalDate.of(2022, 3, 17),
                        "Located in the Camelback Corridor, our Phoenix office is designed for desert climate efficiency. Features include advanced cooling systems, drought-resistant landscaping, and southwestern architectural elements with abundant natural light.",
                        ZoneId.of("America/Phoenix"),
                        generateRandomFacilities()
                ))
        ));

        // Uruguay location
        locationsByCountry.put(Country.ofIsoCode("UY"), List.of(
                locationRepository.insert(new LocationData(
                        "Montevideo",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Plaza Independencia 832, Piso 6, Ciudad Vieja",
                                "Montevideo",
                                null,
                                "11000",
                                Country.ofIsoCode("UY")
                        ),
                        LocalDate.of(2021, 12, 9),
                        "Located in historic Ciudad Vieja, this office combines Uruguay's progressive values with coastal charm. Features include flexible work arrangements and a strong emphasis on employee well-being.",
                        ZoneId.of("America/Montevideo"),
                        generateRandomFacilities()
                ))
        ));

        // Venezuela location
        locationsByCountry.put(Country.ofIsoCode("VE"), List.of(
                locationRepository.insert(new LocationData(
                        "Caracas",
                        LocationType.BRANCH_OFFICE,
                        new InternationalPostalAddress(
                                "Av. Francisco de Miranda, Torre Europa, Piso 14, El Rosal",
                                "Caracas",
                                null,
                                "1060",
                                Country.ofIsoCode("VE")
                        ),
                        LocalDate.of(2020, 7, 8),
                        "Located in El Rosal business district with views of Ávila Mountain, this office serves as our Venezuela hub. Features include secure facilities and community-focused workspaces.",
                        ZoneId.of("America/Caracas"),
                        generateRandomFacilities()
                ))
        ));
    }
}
