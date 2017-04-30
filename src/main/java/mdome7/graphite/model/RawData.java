package mdome7.graphite.model;

import jersey.repackaged.com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * A more compact version of representing a data series.
 */
public class RawData {
    private String name;

    private long startTimestamp;

    private long endTimestamp;

    private int step;

    /** null means no data or "None" */
    private final List<Double> values;

    public RawData() {
        this(null, -1, -1, -1, null);
    }

    public RawData(String name, long start, long end, int step, Double [] values) {
        this.name = name;
        this.startTimestamp = start;
        this.endTimestamp = end;
        this.step = step;
        this.values = values == null ? new ArrayList<>(500) : Lists.newArrayList(values);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void addValue(Double value) {
        this.values.add(value);
    }

    public List<Double> getValues() {
        return values;
    }
}
