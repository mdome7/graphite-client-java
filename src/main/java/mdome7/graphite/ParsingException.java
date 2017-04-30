package mdome7.graphite;

/**
 * response parsing
 */
public class ParsingException extends APIException {
    public ParsingException(String msg) {
        super(msg);
    }

    public ParsingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
