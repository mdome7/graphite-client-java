package mdome7.graphite;

/**
 * Graphite time parameter
 */
public interface PointInTime {

    /** string representation to be passed in as parameter to Graphite */
    String toParamString();
}
