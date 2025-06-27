/*
 * Custom Exception to signal unexpected data format
 */
public class CorruptDataException extends Exception {
    public CorruptDataException() {
        super();
    }

    public CorruptDataException(String message) {
        super(message);
    }

    public CorruptDataException(Throwable cause) {
        super(cause);
    }

    public CorruptDataException(String message, Throwable cause) {
        super(message, cause);
    }
}