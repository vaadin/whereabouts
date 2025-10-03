package com.example.application.common.address;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

class JacksonPostalAddressDeserializer extends ValueDeserializer<PostalAddress> {

    @Override
    public PostalAddress deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JacksonException {
        JsonNode node = jsonParser.readValueAsTree();
        String countryCode = node.required("country").asString();
        return switch (countryCode) {
            case CanadianPostalAddress.ISO_CODE ->
                    deserializationContext.readTreeAsValue(node, CanadianPostalAddress.class);
            case FinnishPostalAddress.ISO_CODE ->
                    deserializationContext.readTreeAsValue(node, FinnishPostalAddress.class);
            case GermanPostalAddress.ISO_CODE ->
                    deserializationContext.readTreeAsValue(node, GermanPostalAddress.class);
            case USPostalAddress.ISO_CODE -> deserializationContext.readTreeAsValue(node, USPostalAddress.class);
            default -> deserializationContext.readTreeAsValue(node, InternationalPostalAddress.class);
        };
    }

}
