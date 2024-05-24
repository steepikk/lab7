package exceptions;

/**
 * Выбрасывается, если пользователь пытается изменить чужой продукт.
 *
 * @author steepikk
 */
public class BadOwnerException extends Exception {
    private final String message;

    public BadOwnerException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
