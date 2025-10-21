package com.example.whereabouts;

import com.example.whereabouts.common.Repository;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

class ArchitectureTest {

    static final String BASE_PACKAGE = "com.example.whereabouts";

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages(BASE_PACKAGE);

    // TODO Add your own rules and remove those that don't apply to your project

    @Test
    void public_repository_methods_should_be_transactional() {
        methods().that()
                .arePublic().and()
                .areDeclaredInClassesThat().areNotInterfaces().and()
                .areDeclaredInClassesThat().areAssignableTo(Repository.class)
                .should()
                .beAnnotatedWith(Transactional.class)
                .check(importedClasses);
    }

    @Test
    void verify_modulith_structure() {
        ApplicationModules.of(Application.class).verify();
    }
}
