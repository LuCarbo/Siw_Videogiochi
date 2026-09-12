package it.uniroma3.siw.exception;

/**
 * Eccezione base per violazioni di regole di business nel Service Layer.
 */
public class BusinessException extends RuntimeException {

    private final String fieldName;

    public BusinessException(String message) {
        super(message);
        this.fieldName = null;
    }

    public BusinessException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
