package input;

import java.util.Scanner;

/**
 * Класс для контроля за потоком ввода
 *
 * @author steepikk
 */

public class InputSteamer {
    private static Scanner scanner;
    private static boolean fileMode = false;

    public static boolean getFileMode() {
        return fileMode;
    }

    public static void setFileMode(boolean mode) {
        fileMode = mode;
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public static void setScanner(Scanner scanner1) {
        scanner = scanner1;
    }
}
