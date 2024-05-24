package cli;

import commands.*;
import exceptions.*;
import input.Console;
import input.InputSteamer;
import network.UDPClient;
import utility.Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Класс, запускающий введенные пользователем команды.
 *
 * @author steepikk
 */

public class Runner {
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
    }

    private final Console console;
    private final UDPClient client;
    private final Map<String, Command> commands;
    private final List<String> scriptStack = new ArrayList<>();

    public Runner(UDPClient client, Console console) {
        InputSteamer.setScanner(new Scanner(System.in));
        this.client = client;
        this.console = console;
        this.commands = new HashMap<>() {{
            put(Commands.AUTH, new AuthenticateCommand(console, client));
            put(Commands.AUTHENTICATE, new AuthenticateCommand(console, client));
            put(Commands.REGISTER, new RegisterCommand(console, client));
            put(Commands.HELP, new HelpCommand(console, client));
            put(Commands.INFO, new InfoCommand(console, client));
            put(Commands.SHOW, new ShowCommand(console, client));
            put(Commands.ADD, new AddCommand(console, client));
            put(Commands.UPDATE, new UpdateCommand(console, client));
            put(Commands.REMOVE_BY_ID, new RemoveCommand(console, client));
            put(Commands.CLEAR, new ClearCommand(console, client));
            put(Commands.COUNT_LESS_THAN_AGE, new CountLessThanAgeCommand(console, client));
            put(Commands.EXECUTE_SCRIPT, new ExecuteScriptCommand(console));
            put(Commands.EXIT, new ExitCommand(console));
            put(Commands.ADD_IF_MAX, new AddIfMaxCommand(console, client));
            put(Commands.FILTER_LESS_THAN_CHARACTER, new FilterLessThanCharacterCommand(console, client));
            put(Commands.HISTORY, new HistoryCommand(console, client));
            put(Commands.PRINT_ASCENDING, new PrintAscendingCommand(console, client));
            put(Commands.REMOVE_GREATER, new RemoveGreaterCommand(console, client));
        }};
    }

    /**
     * Интерактивный режим
     */
    public void interactiveMode() {
        var userScanner = InputSteamer.getScanner();
        try {
            ExitCode commandStatus;
            String[] userCommand = {"", ""};

            do {
                console.ps1();
                userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                commandStatus = launchCommand(userCommand);
            } while (commandStatus != ExitCode.EXIT);

        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    /**
     * Режим для запуска скрипта.
     *
     * @param argument Аргумент скрипта
     * @return Код завершения.
     */
    public ExitCode scriptMode(String argument) {
        String[] userCommand = {"", ""};
        ExitCode commandStatus;
        scriptStack.add(argument);
        if (!new File(argument).exists()) {
            argument = "../" + argument;
        }
        try (Scanner scriptScanner = new Scanner(new File(argument))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = InputSteamer.getScanner();
            InputSteamer.setScanner(scriptScanner);
            InputSteamer.setFileMode(true);

            do {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                while (scriptScanner.hasNextLine() && userCommand[0].isEmpty()) {
                    userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                }
                console.println(console.getPS1() + String.join(" ", userCommand));
                if (userCommand[0].equals("execute_script")) {
                    for (String script : scriptStack) {
                        if (userCommand[1].equals(script)) throw new ScriptRecursionException();
                    }
                }
                commandStatus = launchCommand(userCommand);
            } while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine());

            InputSteamer.setScanner(tmpScanner);
            InputSteamer.setFileMode(true);

            if (commandStatus == ExitCode.ERROR && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                console.println("Проверьте скрипт на корректность введенных данных!");
            }

            return commandStatus;

        } catch (FileNotFoundException exception) {
            console.printError("Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            console.printError("Файл со скриптом пуст!");
        } catch (ScriptRecursionException exception) {
            console.printError("Скрипты не могут вызываться рекурсивно!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return ExitCode.ERROR;
    }

    /**
     * Запускает команду.
     *
     * @param userCommand Команда для запуска
     * @return Код завершения.
     */
    private ExitCode launchCommand(String[] userCommand) {
        if (userCommand[0].equals("")) return ExitCode.OK;
        var command = commands.get(userCommand[0]);

        if (command == null) {
            console.printError("Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
            return ExitCode.ERROR;
        }

        switch (userCommand[0]) {
            case "exit" -> {
                if (!commands.get("exit").apply(userCommand)) return ExitCode.ERROR;
                else return ExitCode.EXIT;
            }
            case "execute_script" -> {
                if (!commands.get("execute_script").apply(userCommand)) return ExitCode.ERROR;
                else return scriptMode(userCommand[1]);
            }
            default -> {
                if (!command.apply(userCommand)) return ExitCode.ERROR;
            }
        }
        ;

        return ExitCode.OK;
    }
}