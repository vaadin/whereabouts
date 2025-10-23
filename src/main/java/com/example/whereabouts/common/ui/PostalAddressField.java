package com.example.whereabouts.common.ui;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.address.*;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

/**
 * @see "Design decision: DD003-20251023-value-objects-and-validation.md"
 */
@NullMarked
public final class PostalAddressField extends CustomField<PostalAddress> {

    private final Select<Country> country;
    private final FormLayout layout;
    private @Nullable AddressForm<?> addressForm;

    public PostalAddressField() {
        // TODO Add row spacing
        country = new Select<>();
        country.setPlaceholder("Country");
        country.setItems(Country.isoCountries());
        country.setItemLabelGenerator(Country::displayName);
        country.addValueChangeListener(this::onCountryValueChange);

        layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("600px", 3)
        );

        layout.add(country, 3);

        add(layout);
    }

    @NullUnmarked
    private void onCountryValueChange(ComponentValueChangeEvent<Select<Country>, Country> event) {
        if (!event.isFromClient()) {
            return;
        }

        var country = event.getValue();

        if (country == null) {
            hideAddressForm();
        } else if (country.isoCode().equals(CanadianPostalAddress.ISO_CODE)) {
            showAddressForm(new CanadianAddressForm());
        } else if (country.isoCode().equals(FinnishPostalAddress.ISO_CODE)) {
            showAddressForm(new FinnishAddressForm());
        } else if (country.isoCode().equals(GermanPostalAddress.ISO_CODE)) {
            showAddressForm(new GermanAddressForm());
        } else if (country.isoCode().equals(USPostalAddress.ISO_CODE)) {
            showAddressForm(new USAddressForm());
        } else {
            showAddressForm(new InternationalAddressForm());
        }
    }

    @Override
    protected @Nullable PostalAddress generateModelValue() {
        return addressForm == null ? null : addressForm.getFormDataObject();
    }

    @Override
    protected void setPresentationValue(@Nullable PostalAddress postalAddress) {
        if (postalAddress == null) {
            hideAddressForm();
        } else {
            switch (postalAddress) {
                case CanadianPostalAddress canadian -> showAddressForm(new CanadianAddressForm(), canadian);
                case FinnishPostalAddress finnish -> showAddressForm(new FinnishAddressForm(), finnish);
                case GermanPostalAddress german -> showAddressForm(new GermanAddressForm(), german);
                case InternationalPostalAddress international ->
                        showAddressForm(new InternationalAddressForm(), international);
                case USPostalAddress us -> showAddressForm(new USAddressForm(), us);
            }
        }
    }

    private void hideAddressForm() {
        if (this.addressForm != null) {
            this.addressForm.dispose();
            this.addressForm = null;
        }
    }

    private void showAddressForm(AddressForm<?> form) {
        hideAddressForm();
        form.init(country, layout);
        form.setReadOnly(isReadOnly());
        this.addressForm = form;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        country.setReadOnly(readOnly);
        if (this.addressForm != null) {
            this.addressForm.setReadOnly(readOnly);
        }
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
        country.setInvalid(invalid);
    }

    // TODO Error message?

    private <F extends AddressForm<T>, T extends Record & PostalAddress> void showAddressForm(F form, T dataObject) {
        showAddressForm(form);
        form.setFormDataObject(dataObject);
    }

    private static sealed abstract class AddressForm<T extends Record & PostalAddress> implements Serializable permits CanadianAddressForm, FinnishAddressForm, GermanAddressForm, InternationalAddressForm, USAddressForm {

        protected final Binder<T> binder;
        private Binder.@Nullable Binding<? extends PostalAddress, Country> countryBinding;

        public AddressForm(Class<T> formDataObjectType) {
            binder = new Binder<>(formDataObjectType);
        }

        final void dispose() {
            if (countryBinding != null) {
                countryBinding.unbind();
                countryBinding = null;
            }
            removeFieldsFromLayout();
        }

        final void init(Select<Country> country, FormLayout layout) {
            countryBinding = binder.forField(country).bind(PostalAddress.PROP_COUNTRY);
            addFieldsToLayout(layout);
        }

        protected abstract void addFieldsToLayout(FormLayout layout);

        protected abstract void removeFieldsFromLayout();

        final void setFormDataObject(T formDataObject) {
            binder.readRecord(formDataObject);
        }

        @Nullable
        final T getFormDataObject() {
            try {
                return binder.writeRecord();
            } catch (ValidationException ex) {
                return null;
            }
        }

        abstract void setReadOnly(boolean readOnly);
    }

    private static final class CanadianAddressForm extends AddressForm<CanadianPostalAddress> {
        private final TextField streetAddress = new TextField();
        private final TextField city = new TextField();
        private final Select<CanadianProvince> province = new Select<>();
        private final TextField postalCode = new TextField();

        CanadianAddressForm() {
            super(CanadianPostalAddress.class);
            streetAddress.setPlaceholder("Street Address");
            streetAddress.setMaxLength(CanadianPostalAddress.MAX_STRING_LENGTH);
            city.setPlaceholder("City");
            city.setMaxLength(CanadianPostalAddress.MAX_STRING_LENGTH);
            province.setPlaceholder("Province");
            province.setItems(CanadianProvince.values());
            province.setItemLabelGenerator(CanadianProvince::displayName);
            postalCode.setPlaceholder("Postal Code");

            binder.forField(streetAddress).bind(CanadianPostalAddress.PROP_STREET_ADDRESS);
            binder.forField(city).bind(CanadianPostalAddress.PROP_CITY);
            binder.forField(province).bind(CanadianPostalAddress.PROP_PROVINCE);
            binder.forField(postalCode)
                    .withConverter(new ValueObjectStringConverter<>(CanadianPostalCode::of))
                    .bind(CanadianPostalAddress.PROP_POSTAL_CODE);
        }

        @Override
        protected void removeFieldsFromLayout() {
            postalCode.removeFromParent();
            province.removeFromParent();
            city.removeFromParent();
            streetAddress.removeFromParent();
        }

        @Override
        protected void addFieldsToLayout(FormLayout layout) {
            layout.add(streetAddress, 3);
            layout.add(city);
            layout.add(province);
            layout.add(postalCode);
        }

        @Override
        void setReadOnly(boolean readOnly) {
            streetAddress.setReadOnly(readOnly);
            city.setReadOnly(readOnly);
            province.setReadOnly(readOnly);
            postalCode.setReadOnly(readOnly);
        }
    }

    private static final class FinnishAddressForm extends AddressForm<FinnishPostalAddress> {
        final TextField streetAddress = new TextField();
        final TextField postalCode = new TextField();
        final TextField postOffice = new TextField();

        FinnishAddressForm() {
            super(FinnishPostalAddress.class);
            streetAddress.setPlaceholder("Street Address");
            streetAddress.setMaxLength(FinnishPostalAddress.MAX_STRING_LENGTH);
            postalCode.setPlaceholder("Postal Code");
            postOffice.setPlaceholder("Post Office");
            postOffice.setMaxLength(FinnishPostalAddress.MAX_STRING_LENGTH);

            binder.forField(streetAddress).bind(FinnishPostalAddress.PROP_STREET_ADDRESS);
            binder.forField(postalCode)
                    .withConverter(new ValueObjectStringConverter<>(FinnishPostalCode::of))
                    .bind(FinnishPostalAddress.PROP_POSTAL_CODE);
            binder.forField(postOffice).bind(FinnishPostalAddress.PROP_POST_OFFICE);
        }

        @Override
        protected void removeFieldsFromLayout() {
            postOffice.removeFromParent();
            postalCode.removeFromParent();
            streetAddress.removeFromParent();
        }

        @Override
        protected void addFieldsToLayout(FormLayout layout) {
            layout.add(streetAddress, 3);
            layout.add(postalCode);
            layout.add(postOffice, 2);
        }

        @Override
        void setReadOnly(boolean readOnly) {
            streetAddress.setReadOnly(readOnly);
            postalCode.setReadOnly(readOnly);
            postOffice.setReadOnly(readOnly);
        }
    }

    private static final class GermanAddressForm extends AddressForm<GermanPostalAddress> {
        final TextField streetAddress = new TextField();
        final TextField postalCode = new TextField();
        final TextField city = new TextField();

        GermanAddressForm() {
            super(GermanPostalAddress.class);
            streetAddress.setPlaceholder("Street Address");
            streetAddress.setMaxLength(GermanPostalAddress.MAX_STRING_LENGTH);
            postalCode.setPlaceholder("Postal Code");
            city.setPlaceholder("City");
            city.setMaxLength(GermanPostalAddress.MAX_STRING_LENGTH);

            binder.forField(streetAddress).bind(GermanPostalAddress.PROP_STREET_ADDRESS);
            binder.forField(postalCode)
                    .withConverter(new ValueObjectStringConverter<>(GermanPostalCode::of))
                    .bind(GermanPostalAddress.PROP_POSTAL_CODE);
            binder.forField(city).bind(GermanPostalAddress.PROP_CITY);
        }

        @Override
        protected void removeFieldsFromLayout() {
            city.removeFromParent();
            postalCode.removeFromParent();
            streetAddress.removeFromParent();
        }

        @Override
        protected void addFieldsToLayout(FormLayout layout) {
            layout.add(streetAddress, 3);
            layout.add(postalCode);
            layout.add(city, 2);
        }

        @Override
        void setReadOnly(boolean readOnly) {
            streetAddress.setReadOnly(readOnly);
            postalCode.setReadOnly(readOnly);
            city.setReadOnly(readOnly);
        }
    }

    private static final class InternationalAddressForm extends AddressForm<InternationalPostalAddress> {
        final TextField streetAddress = new TextField();
        final TextField city = new TextField();
        final TextField stateProvinceOrRegion = new TextField();
        final TextField postalCode = new TextField();

        InternationalAddressForm() {
            super(InternationalPostalAddress.class);
            streetAddress.setPlaceholder("Street Address");
            streetAddress.setMaxLength(InternationalPostalAddress.MAX_STRING_LENGTH);
            city.setPlaceholder("City");
            city.setMaxLength(InternationalPostalAddress.MAX_STRING_LENGTH);
            stateProvinceOrRegion.setPlaceholder("State, Province or Region");
            stateProvinceOrRegion.setMaxLength(InternationalPostalAddress.MAX_STRING_LENGTH);
            postalCode.setPlaceholder("Postal Code");
            postalCode.setMaxLength(InternationalPostalAddress.MAX_STRING_LENGTH);

            binder.forField(streetAddress).bind(InternationalPostalAddress.PROP_STREET_ADDRESS);
            binder.forField(city).bind(InternationalPostalAddress.PROP_CITY);
            binder.forField(stateProvinceOrRegion).bind(InternationalPostalAddress.PROP_STATE_PROVINCE_OR_REGION);
            binder.forField(postalCode).bind(InternationalPostalAddress.PROP_POSTAL_CODE);
        }

        @Override
        protected void removeFieldsFromLayout() {
            postalCode.removeFromParent();
            stateProvinceOrRegion.removeFromParent();
            city.removeFromParent();
            streetAddress.removeFromParent();
        }

        @Override
        protected void addFieldsToLayout(FormLayout layout) {
            layout.add(streetAddress, 3);
            layout.add(city);
            layout.add(stateProvinceOrRegion);
            layout.add(postalCode);
        }

        @Override
        void setReadOnly(boolean readOnly) {
            streetAddress.setReadOnly(readOnly);
            city.setReadOnly(readOnly);
            stateProvinceOrRegion.setReadOnly(readOnly);
            postalCode.setReadOnly(readOnly);
        }
    }

    private static final class USAddressForm extends AddressForm<USPostalAddress> {
        final TextField streetAddress = new TextField();
        final TextField city = new TextField();
        final Select<USState> state = new Select<>();
        final TextField zipCode = new TextField();

        USAddressForm() {
            super(USPostalAddress.class);
            streetAddress.setPlaceholder("Street Address");
            streetAddress.setMaxLength(USPostalAddress.MAX_STRING_LENGTH);
            city.setPlaceholder("City");
            city.setMaxLength(USPostalAddress.MAX_STRING_LENGTH);
            state.setPlaceholder("State");
            state.setItems(USState.values());
            state.setItemLabelGenerator(USState::displayName);
            zipCode.setPlaceholder("Zip Code");

            binder.forField(streetAddress).bind(USPostalAddress.PROP_STREET_ADDRESS);
            binder.forField(city).bind(USPostalAddress.PROP_CITY);
            binder.forField(state).bind(USPostalAddress.PROP_STATE);
            binder.forField(zipCode)
                    .withConverter(new ValueObjectStringConverter<>(USZipCode::of))
                    .bind(USPostalAddress.PROP_ZIP_CODE);
        }

        @Override
        protected void removeFieldsFromLayout() {
            zipCode.removeFromParent();
            state.removeFromParent();
            city.removeFromParent();
            streetAddress.removeFromParent();
        }

        @Override
        protected void addFieldsToLayout(FormLayout layout) {
            layout.add(streetAddress, 3);
            layout.add(city);
            layout.add(state);
            layout.add(zipCode);
        }

        @Override
        void setReadOnly(boolean readOnly) {
            streetAddress.setReadOnly(readOnly);
            city.setReadOnly(readOnly);
            state.setReadOnly(readOnly);
            zipCode.setReadOnly(readOnly);
        }
    }
}
