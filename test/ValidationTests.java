import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.ValidationUtils;
import utils.exceptions.InvalidInputException;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidationTests {

    @Mock
    private Scanner scanner;

    private String prompt;

    @BeforeEach
    void setUp() {
        prompt = "Enter budget: ";
    }

    @Test
    void budgetOfZeroThrows() {
        when(scanner.nextLine()).thenReturn("0");
        assertThrows(InvalidInputException.class, () -> ValidationUtils.readBudget(scanner, prompt));
        verify(scanner).nextLine();
    }

    @Test
    void budgetBelowZeroThrows() {
        when(scanner.nextLine()).thenReturn("-5");
        assertThrows(InvalidInputException.class, () -> ValidationUtils.readBudget(scanner, prompt));
        verify(scanner).nextLine();
    }
}
