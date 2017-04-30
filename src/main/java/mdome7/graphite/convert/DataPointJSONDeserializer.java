package mdome7.graphite.convert;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import mdome7.graphite.model.DataPoint;

import java.io.IOException;

/**
 * Custom deserializer for Data Point
 */
public class DataPointJSONDeserializer extends JsonDeserializer<DataPoint> {

    @Override
    public DataPoint deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.readValueAsTree();
        if (node.isArray() && node.size() == 2) {
            Double value = null;
            long timestamp = -1;
            JsonNode valueNode = node.get(0);
            if (!valueNode.isNull()) {
                value = valueNode.asDouble();
            }
            JsonNode timestampNode = node.get(1);
            if (!timestampNode.isNull()) {
                timestamp = timestampNode.asLong();
            } else {
                throw new JsonParseException(jp, "DataPoint timestamp is null: " + node.toString());
            }
            return new DataPoint(value, timestamp);
        } else {
            throw new JsonParseException(jp, "JSON must be an array of length 2: " + node.toString());
        }
    }
}
