import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ValidationUtils;
import utils.exceptions.InvalidInputException;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTests {

    private String prompt;

    @BeforeEach
    void setUp() {
        prompt = "Enter budget: ";
    }

    @Test
    void budgetOfZeroThrows() {
        Scanner scanner = new Scanner("0\n");
        assertThrows(InvalidInputException.class, () -> ValidationUtils.readBudget(scanner, prompt));
    }

    @Test
    void budgetBelowZeroThrows() {
        Scanner scanner = new Scanner("-5\n");
        assertThrows(InvalidInputException.class, () -> ValidationUtils.readBudget(scanner, prompt));
    }
}
