package mdome7.graphite.model;

/**
 * Value and timestamp
 */
public class DataPoint {

    private Double value;

    private long timestamp;

    public DataPoint() {}

    public DataPoint(Double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
