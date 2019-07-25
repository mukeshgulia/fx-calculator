package fxCalculator.app;

import com.anz.interview.fxCalculator.app.Application;
import com.anz.interview.fxCalculator.dto.FxState;
import com.anz.interview.fxCalculator.exception.DataStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestApplication {

    private static FxState state;
    private static Application application;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeAll
    static void setup() throws DataStateException {
        state = new FxState(); //load all data in resources
        application = new Application(state);
    }

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testUnityConversion() {
        String command = "AUD 100.00 in AUD";
        application.execute(command);
        assertEquals("AUD 100.00 = AUD 100.00", outContent.toString());
    }

    @Test
    void testDirectConversion() {
        String command = "AUD 100.00 in USD";
        application.execute(command);
        assertEquals("AUD 100.00 = USD 83.71", outContent.toString());
    }

    @Test
    void testRoutedConversion() {
        String command = "AUD 100.00 in DKK";
        application.execute(command);
        assertEquals("AUD 100.00 = DKK 505.76", outContent.toString());
    }

    @Test
    void testInvertedConversion() {
        String command = "JPY 100 in USD";
        application.execute(command);
        assertEquals("JPY 100 = USD 0.83", outContent.toString());
    }

    @Test
    void testConversionWhenBaseCurrencyIsInvalid() {

        String command = "KWR 1000.00 in USD";
        application.execute(command);
        assertEquals("Unable to find rate for KWR/USD", outContent.toString());
    }

    @Test
    void testConversionWhenTermCurrencyIsInvalid() {
        String command = "USD 1000.00 in KWR";
        application.execute(command);
        assertEquals("Unable to find rate for USD/KWR", outContent.toString());
    }

    @Test
    void testConversionWhenBothCurrenciesInvalid() {
        String command = "FJK 1000.00 in KWR";
        application.execute(command);
        assertEquals("Unable to find rate for FJK/KWR", outContent.toString());
    }

    @Test
    void testQuit(){
        application.execute("Q");
        assertFalse(application.isRunState());
    }

    @Test
    void testInvalidCommand() {
        application.execute("invalid command");

        String error = "Incorrect parameters in command" + "\r\n";
        String help = "Usage:<ccy1> <amount> in <ccy2>.\nExample: AUD 100.00 in USD"+ "\r\n";

        assertEquals(error+help, outContent.toString());
        //assertEquals(help, outContent.toString());
    }

    @Test
    void testValidCommandInvalidAmountParams() {
        application.execute("AUD 76.e in USD");

        String error = "Amount provided is not a number:76.e" + "\r\n";
        String help = "Usage:<ccy1> <amount> in <ccy2>.\nExample: AUD 100.00 in USD"+ "\r\n";

        assertEquals(error+help, outContent.toString());
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

}

