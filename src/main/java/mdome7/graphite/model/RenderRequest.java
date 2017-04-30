package mdome7.graphite.model;

import mdome7.graphite.PointInTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for the Render API.
 */
public class RenderRequest {

    /** one or more targets */
    private List<String> targets;

    private PointInTime from;

    private PointInTime until;

    public RenderRequest(String target) {
        this.targets = new ArrayList<String>();
        this.targets.add(target);
    }

    public void addTarget(String target) {
        this.targets.add(target);
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setFrom(PointInTime from) {
        this.from = from;
    }

    public PointInTime getFrom() {
        return from;
    }

    public void setUntil(PointInTime until) {
        this.until = until;
    }

    public PointInTime getUntil() {
        return until;
    }
}
