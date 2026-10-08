import utils.ConsoleMenu;

import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {
        try {
            ConsoleMenu menu = new ConsoleMenu();
            menu.start();
        } catch (NoSuchElementException ex) {
            System.out.println();
            System.out.println("Goodbye.");
        }
    }
}
