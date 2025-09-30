package com.example.application.common.address;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.jooq.exception.DataAccessException;

import java.io.IOException;

class PostalAddressDeserializer extends JsonDeserializer<PostalAddress> {

    @Override
    public PostalAddress deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.readValueAsTree();
        String countryCode = node.required("country").asText();
        var codec = jsonParser.getCodec();

        try {
            return switch (countryCode) {
                case CanadianPostalAddress.ISO_CODE -> codec.treeToValue(node, CanadianPostalAddress.class);
                case FinnishPostalAddress.ISO_CODE -> codec.treeToValue(node, FinnishPostalAddress.class);
                case GermanPostalAddress.ISO_CODE -> codec.treeToValue(node, GermanPostalAddress.class);
                case USPostalAddress.ISO_CODE -> codec.treeToValue(node, USPostalAddress.class);
                default -> codec.treeToValue(node, InternationalPostalAddress.class);
            };
        } catch (IOException ex) {
            throw new DataAccessException("Error reading address JSON", ex);
        }
    }
}
