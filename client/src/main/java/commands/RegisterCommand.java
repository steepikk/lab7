package commands;

import auth.SessionHandler;
import exceptions.APIException;
import exceptions.*;
import input.Console;
import input.RegisterInput;
import network.UDPClient;
import network.requests.RegisterRequest;
import network.responses.RegisterResponse;

import java.io.IOException;

/**
 * Команда 'register'. Регистрирует пользователя.
 *
 * @author steepikk
 */
public class RegisterCommand extends Command {
    private final Console console;
    private final UDPClient client;

    public RegisterCommand(Console console, UDPClient client) {
        super("register");
        this.console = console;
        this.client = client;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new WrongAmountOfElementsException();
            console.println("Создание пользователя:");

            var user = (new RegisterInput(console)).build();

            var response = (RegisterResponse) client.sendAndReceiveCommand(new RegisterRequest(user));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            SessionHandler.setCurrentUser(response.user);
            console.println("Пользователь " + response.user.getName() +
                    " с id=" + response.user.getId() + " успешно создан!");
            return true;

        } catch (WrongAmountOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
        } catch (InvalidFormException exception) {
            console.printError("Введенные данные не валидны! Пользователь на зарегистрирован");
        } catch (IOException e) {
            console.printError("Ошибка взаимодействия с сервером");
        } catch (APIException e) {
            console.printError(e.getMessage());
        } catch (IncorrectScriptException ignored) {
        } catch (ErrorResponseException e) {
            console.printError(e.getMessage());
        }
        return false;
    }
}
