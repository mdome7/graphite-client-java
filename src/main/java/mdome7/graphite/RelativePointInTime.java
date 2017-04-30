package mdome7.graphite;

/**
 * Relative point in time
 */
public class RelativePointInTime implements PointInTime {

    public enum TimeUnit {
        Seconds("s"), Minutes("min"), Hours("h"), Days("d"), Weeks("w"), Month("mon"), Year("y");

        private String shortName;

        TimeUnit(String shortName) {
            this.shortName = shortName;
        }

        public String getShortName() { return this.shortName; }
    }

    private final int value;

    private final TimeUnit timeUnit;

    public RelativePointInTime(int value, TimeUnit timeUnit) {
        this.value = value;
        this.timeUnit = timeUnit;
    }

    public int getValue() {
        return value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String toParamString() {
        return "-" + this.value + this.timeUnit.shortName;
    }
}
