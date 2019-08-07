package fxCalculator.app;

import com.anz.interview.fxCalculator.app.FxCalculatorService;
import com.anz.interview.fxCalculator.dto.FxState;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FxState.class, FxCalculatorService.class})
public class FxCalculatorServiceTest {

    @Autowired
    private FxCalculatorService fxCalculatorService;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testUnityConversion() {
        String command = "AUD 100.00 in AUD";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("AUD 100.00 = AUD 100.00"));
    }

    @Test
    void testDirectConversion() {
        String command = "AUD 100.00 in USD";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("AUD 100.00 = USD 83.71"));
    }

    @Test
    void testRoutedConversion() {
        String command = "AUD 100.00 in DKK";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("AUD 100.00 = DKK 505.76"));
    }

    @Test
    void testInvertedConversion() {
        String command = "JPY 100 in USD";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("JPY 100 = USD 0.83"));
    }

    @Test
    void testConversionWhenBaseCurrencyIsInvalid() {

        String command = "KWR 1000.00 in USD";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("Unable to find rate for KWR/USD"));
    }

    @Test
    void testConversionWhenTermCurrencyIsInvalid() {
        String command = "USD 1000.00 in KWR";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("Unable to find rate for USD/KWR"));
    }

    @Test
    void testConversionWhenBothCurrenciesInvalid() {
        String command = "FJK 1000.00 in KWR";
        fxCalculatorService.execute(command);
        assertThat(outContent.toString(), containsString("Unable to find rate for FJK/KWR"));
    }

    @Test
    void testQuit(){
        fxCalculatorService.execute("Q");
        assertFalse(fxCalculatorService.isRunState());
    }

    @Test
    void testInvalidCommand() {
        fxCalculatorService.execute("invalid command");

        String error = "Incorrect parameters in command";
        String helpUsage = "Usage:<ccy1> <amount> in <ccy2>";
        String helpExample = "Example: AUD 100.00 in USD";

        assertThat(outContent.toString(), containsString(error));
        assertThat(outContent.toString(), containsString(helpUsage));
        assertThat(outContent.toString(), containsString(helpExample));
    }

    @Test
    void testValidCommandInvalidAmountParams() {
        fxCalculatorService.execute("AUD 76.e in USD");

        String error = "Amount provided is not a number:76.e";
        String helpUsage = "Usage:<ccy1> <amount> in <ccy2>";
        String helpExample = "Example: AUD 100.00 in USD";

        assertThat(outContent.toString(), containsString(error));
        assertThat(outContent.toString(), containsString(helpUsage));
        assertThat(outContent.toString(), containsString(helpExample));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

}

