package input;

import exceptions.IncorrectScriptException;
import exceptions.InvalidFormException;
import exceptions.MustBeNotEmptyException;
import user.User;

import java.util.NoSuchElementException;


/**
 * Класс для ввода пользователя
 *
 * @author steepikk
 */
public class UserInput {
    private final Console console;

    public UserInput(Console console) {
        this.console = console;
    }

    public User build() throws InvalidFormException, IncorrectScriptException {
        var user = new User(0, inputName(), inputPassword());
        if (!user.validate()) throw new InvalidFormException();
        return user;
    }

    /**
     * Ввод имени
     */
    protected String inputName() throws IncorrectScriptException {
        String name;
        var fileMode = InputSteamer.getFileMode();
        while (true) {
            try {
                console.println("Введите имя пользоваателя:");
                console.ps2();

                name = InputSteamer.getScanner().nextLine().trim();
                if (fileMode) console.println(name);

                if (name.equals("") || name.length() >= 40) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Имя пользователя не распознано!");
                if (fileMode) throw new IncorrectScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Размер имени должен быть от 1 до 40 символов!");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }

        return name;
    }

    protected String inputPassword() throws IncorrectScriptException {
        String password;
        var fileMode = InputSteamer.getFileMode();
        while (true) {
            try {
                console.println("Введите пароль пользователя:");
                console.ps2();

                password = readPassword();
                if (fileMode) console.println(password);

                if (password.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Пароль пользователя не распознан!");
                if (fileMode) throw new IncorrectScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Пароль не должен быть пустым!");
                if (fileMode) throw new IncorrectScriptException();
            } catch (IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }

        return password;
    }

    protected String readPassword() {
        if (System.console() == null) {
            return InputSteamer.getScanner().nextLine().trim();
        }
        return new String(System.console().readPassword());
    }
}
