package it.uniroma3.siw.exception;

/**
 * Eccezione sollevata dal Service Layer quando si tenta di inserire
 * un'entità duplicata (es. username, email, titolo videogioco).
 */
public class DuplicateEntityException extends BusinessException {

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String fieldName, String message) {
        super(fieldName, message);
    }
}
