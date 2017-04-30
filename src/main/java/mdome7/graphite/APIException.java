package mdome7.graphite;

/**
 * Generic api exception
 */
public class APIException extends Exception {

    private Integer httpStatus;

    public APIException(String msg) {
        super(msg);
    }

    public APIException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public APIException(Integer httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public APIException(Integer httpStatus, String msg, Throwable cause) {
        super(msg, cause);
        this.httpStatus = httpStatus;
    }
}
