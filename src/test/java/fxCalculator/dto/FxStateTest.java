package fxCalculator.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.anz.interview.fxCalculator.dto.FxState;
import com.anz.interview.fxCalculator.exception.DataStateException;
import com.anz.interview.fxCalculator.model.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;


public class FxStateTest {

    private static FxState state;

    @BeforeAll
    static void setup() throws DataStateException {
        state = new FxState(); //load all data in resources
    }

    @Test
    void getCurrencyByNameThatExists() {
        String existingCurrency = "AUD";
        Optional<Currency> currency = state.getCurrencyByName(existingCurrency);
        assertTrue(currency.isPresent());
    }

    @Test
    void getCurrencyByNameThatDoesNotExists() {
        String existingCurrency = "KWR";
        Optional<Currency> currency = state.getCurrencyByName(existingCurrency);
        assertFalse(currency.isPresent());
    }

    @Test
    void getRateWhenBaseAndTermExists() throws DataStateException {
        double rate = state.getRate("AUD", "USD");
        assertEquals(0.8371, rate);
    }

    @Test
    void getRateWhenBaseDoesNotExists() {

        Exception exception = assertThrows(DataStateException.class,
                () -> state.getRate("KWR", "USD"));

        String expectedMessage = "Currency does not exist:KWR";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getRateWhenTermMappingDoesNotExistsForBase() {

        Exception exception = assertThrows(DataStateException.class,
                () -> state.getRate("AUD", "EUR"));

        String expectedMessage = "Rate does not exist for base/term:AUD/EUR";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getRatesMatrix() {
        String[][] expected = {
                {"/", "AUD", "CAD", "CNY", "CZK", "DKK", "EUR", "GBP", "JPY", "NOK", "NZD", "USD"},
                {"AUD", "1:1", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "D"},
                {"CAD", "USD", "1:1", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "D"},
                {"CNY", "USD", "USD", "1:1", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "Inv"},
                {"CZK", "USD", "USD", "USD", "1:1", "EUR", "Inv", "USD", "USD", "EUR", "USD", "EUR"},
                {"DKK", "USD", "USD", "USD", "EUR", "1:1", "Inv", "USD", "USD", "EUR", "USD", "EUR"},
                {"EUR", "USD", "USD", "USD", "D", "D", "1:1", "USD", "USD", "D", "USD", "D"},
                {"GBP", "USD", "USD", "USD", "USD", "USD", "USD", "1:1", "USD", "USD", "USD", "D"},
                {"JPY", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "1:1", "USD", "USD", "Inv"},
                {"NOK", "USD", "USD", "USD", "EUR", "EUR", "Inv", "USD", "USD", "1:1", "USD", "EUR"},
                {"NZD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "1:1", "D"},
                {"USD", "Inv", "Inv", "D", "EUR", "EUR", "Inv", "Inv", "D", "EUR", "Inv", "1:1"}
        };

        IntStream.range(0, expected.length)
                .forEach(index ->
                        assertEquals(
                                Arrays.toString(expected[index]),
                                Arrays.toString(state.getRateMatrix()[index])));

    }

    @Test
    void getBasePosition() {
        assertEquals(1, state.getBasePosition("AUD"));
    }

    @Test
    void getBasePositionNegativeCase() {
        assertEquals(-1, state.getBasePosition("KWR"));
    }

    @Test
    void getTermPosition() {
        assertEquals(1, state.getTermPosition("AUD"));
    }

    @Test
    void getTermPositionNegativeCase() {
        assertEquals(-1, state.getTermPosition("KWR"));
    }

}