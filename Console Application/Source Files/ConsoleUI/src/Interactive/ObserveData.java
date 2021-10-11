package Interactive;

import Database.DTO;
import Database.EvoLoader;
import Printers.EvoPrinter;
import Printers.Printer;

public class ObserveData {

    /**
     * Show the user the current system configurations
     */
    public static void showSystem() throws Exception
    {
        if(Menu.solutionLoader == null || Menu.evoLoader == null)
        {
            throw new Exception("Please load data from XML before trying to use this command!");
        }

        DTO dataToObserve = Menu.solutionLoader.getSystemProperties();
        Menu.solutionPrinter = (Printer) Menu.CURRENT_APP_SOLUTION_PRINTER_CLASS.newInstance();

        Menu.solutionPrinter.PrintProperties(dataToObserve);

        dataToObserve = EvoLoader.getSystemProperties();

        Printer evoPrinter = new EvoPrinter();
        evoPrinter.PrintProperties(dataToObserve);
    }
}
