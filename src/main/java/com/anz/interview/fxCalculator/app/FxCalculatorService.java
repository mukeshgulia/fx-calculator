package com.anz.interview.fxCalculator.app;

import com.anz.interview.fxCalculator.dto.FxState;
import com.anz.interview.fxCalculator.exception.CommandException;
import com.anz.interview.fxCalculator.exception.DataStateException;
import com.anz.interview.fxCalculator.model.Command;
import com.anz.interview.fxCalculator.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class FxCalculatorService {

    private FxState fxData;
    private boolean runState;

    @Autowired
    public FxCalculatorService(FxState fxData) {
        this.fxData = fxData;
    }

    public void start() {
        runState = true;
        Scanner scanner = new Scanner(System.in);
        while (runState) {
            System.out.println();
            System.out.print("%> ");
            String newCommand = scanner.nextLine();
            execute(newCommand);
        }
        scanner.close();
    }

    public void execute(String newCommand) {

        try {

            Command command = extractCommand(newCommand);
            if (command.isQuitCommand()) {
                setRunState(false);
                return;
            }

            Optional<Currency> base = fxData.getCurrencyByName(command.getCcy1());
            Optional<Currency> term = fxData.getCurrencyByName(command.getCcy2());

            if (!base.isPresent() || !term.isPresent()) {
                throw new DataStateException("Unable to find rate for "
                        + command.getCcy1() + '/' + command.getCcy2());
            }

            double rate = calculateRate(command);
            double convertedAmount = rate * command.getAmount();
            String output = (base.get().getName())
                    + (" ")
                    + (base.get().getFormat().format(command.getAmount()))
                    + (" = ")
                    + (term.get().getName())
                    + (" ")
                    + (term.get().getFormat().format(convertedAmount));

            System.out.print(output);

        } catch (CommandException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getUsage());
        } catch (DataStateException e) {
            System.out.print(e.getMessage());
        }
    }

    private Command extractCommand(String newCommand) throws CommandException {

        if (newCommand.equalsIgnoreCase("q")) {
            return new Command(true);
        }

        String[] data = newCommand.split(" ");
        try {
            return new Command(data[0], data[3], Double.valueOf(data[1]));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Incorrect parameters in command");
        } catch (NumberFormatException e) {
            throw new CommandException("Amount provided is not a number:" + data[1]);
        }
    }

    private Double calculateRate(Command command) throws DataStateException {

        int basePos = fxData.getBasePosition(command.getCcy1());
        int termPos = fxData.getTermPosition(command.getCcy2());
        double rate = 1;  // 1:1 case

        if (!fxData.getRateMatrix()[basePos][termPos].equalsIgnoreCase("1:1")) {
            if (fxData.getRateMatrix()[basePos][termPos].equalsIgnoreCase("d")) {
                rate = fxData.getRate(command.getCcy1(), command.getCcy2());
            } else if (fxData.getRateMatrix()[basePos][termPos].equalsIgnoreCase("inv")) {
                rate = 1 / fxData.getRate(command.getCcy2(), command.getCcy1());
            } else {
                Command left = new Command(command.getCcy1(), fxData.getRateMatrix()[basePos][termPos], null);
                Command right = new Command(fxData.getRateMatrix()[basePos][termPos], command.getCcy2(), null);
                rate = calculateRate(left) * calculateRate(right);
            }
        }
        return rate;
    }

    public boolean isRunState() {
        return runState;
    }

    private void setRunState(boolean runState) {
        this.runState = runState;
    }
}
