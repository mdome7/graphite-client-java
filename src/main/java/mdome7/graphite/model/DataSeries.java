package mdome7.graphite.model;

import java.util.List;

/**
 * Values
 */
public class DataSeries {

    /** target name */
    private String target;

    private List<DataPoint> datapoints;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<DataPoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<DataPoint> datapoints) {
        this.datapoints = datapoints;
    }
}
