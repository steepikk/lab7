package input;


import exceptions.*;
import user.User;

import java.util.NoSuchElementException;

/**
 * Класс для обработки ввода данных при регистрации нового пользователя.
 *
 * @author steepikk
 */
public class RegisterInput extends UserInput {
    private final Console console;

    /**
     * Конструктор для создания объекта RegisterInput.
     *
     * @param console Объект консоли для ввода-вывода.
     */
    public RegisterInput(Console console) {
        super(console);
        this.console = console;
    }

    /**
     * Строит объект пользователя на основе введенных данных.
     *
     * @return Объект пользователя.
     * @throws InvalidFormException     Если данные не прошли валидацию.
     * @throws IncorrectScriptException Если скрипт ввода содержит некорректные данные.
     */
    @Override
    public User build() throws InvalidFormException, IncorrectScriptException {
        var user = new User(0, inputName(), inputPassword());
        if (!user.validate()) throw new InvalidFormException();
        return user;
    }

    /**
     * Ввод пароля пользователя.
     *
     * @return Пароль пользователя.
     * @throws IncorrectScriptException Если скрипт ввода содержит некорректные данные.
     */
    @Override
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

                console.println("Повторите пароль пользователя:");
                console.ps2();

                var passwordRepeat = readPassword();
                if (fileMode) console.println(passwordRepeat);

                if (!password.equals(passwordRepeat)) throw new InvalidFormException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Пароль пользователя не распознан!");
                if (fileMode) throw new IncorrectScriptException();
            } catch (InvalidFormException exception) {
                console.printError("Введенные пароли не совпадают!");
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

}
