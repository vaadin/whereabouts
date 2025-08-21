package com.example.application;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArchitectureTest {

    static final String BASE_PACKAGE = "com.example.application";

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages(BASE_PACKAGE);

    // TODO Add your own rules and remove those that don't apply to your project

    @Test
    void domain_model_should_not_depend_on_application_services() {
        noClasses().that().resideInAPackage(BASE_PACKAGE + "..domain..").should().dependOnClassesThat()
                .resideInAPackage(BASE_PACKAGE + "..service..").check(importedClasses);
    }

    @Test
    void domain_model_should_not_depend_on_the_user_interface() {
        noClasses().that().resideInAPackage(BASE_PACKAGE + "..domain..").should().dependOnClassesThat()
                .resideInAnyPackage(BASE_PACKAGE + "..ui..").check(importedClasses);
    }

    @Test
    void repositories_should_only_be_used_by_application_services_and_other_domain_classes() {
        classes().that().areAssignableTo(Repository.class).should().onlyHaveDependentClassesThat()
                .resideInAnyPackage(BASE_PACKAGE + "..domain..", BASE_PACKAGE + "..service..").check(importedClasses);
    }

    @Test
    void repositories_should_only_be_called_by_transactional_methods() {
        methods().that().areDeclaredInClassesThat().areAssignableTo(Repository.class).should().onlyBeCalled()
                .byMethodsThat(annotatedWith(Transactional.class)).check(importedClasses);
    }

    @Test
    void application_services_should_not_depend_on_the_user_interface() {
        noClasses().that().resideInAPackage(BASE_PACKAGE + "..service..").should().dependOnClassesThat()
                .resideInAnyPackage(BASE_PACKAGE + "..ui..").check(importedClasses);
    }

    @Test
    void there_should_not_be_circular_dependencies_between_feature_packages() {
        slices().matching(BASE_PACKAGE + ".(*)..").should().beFreeOfCycles().check(importedClasses);
    }

    @Test
    void security_package_should_not_depend_on_other_application_classes() {
        classes().that().resideInAPackage(BASE_PACKAGE + ".security..").should().onlyAccessClassesThat()
                .resideOutsideOfPackage(BASE_PACKAGE + "..").orShould().accessClassesThat()
                .resideInAPackage(BASE_PACKAGE + ".security..")
                .because("Security classes should only depend on external libraries and other security classes");
    }
}
