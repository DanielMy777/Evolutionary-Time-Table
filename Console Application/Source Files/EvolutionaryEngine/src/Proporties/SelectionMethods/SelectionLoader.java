package Proporties.SelectionMethods;

import Management.EEException;
import Proporties.Selection;
import generated.ETTSelection;

//This utility class can load a Selection method from a ETTSelection object generated by unmarshalling.

public class SelectionLoader {
    public static Selection LoadSelectionFromXML(ETTSelection selection) throws EEException
    {
        Selection res = null;
        int elitism = 0;
        String[] confArr = new String[3];
        String conf = selection.getConfiguration();
        if(conf == null)
            conf = "";
        else
            confArr = conf.split(",");
        switch (selection.getType())
        {
            case "Truncation":
                int top = Integer.parseInt(confArr[0].split("=")[1]);
                if(top < 0 || top > 100)
                    throw new EEException("XML error - top percent must be 0 - 100");
                res = new Truncation(elitism, conf, top);
                break;
            case "RouletteWheel":
                res = new RouletteWheel(elitism,conf);
                break;
            case "Tournament":
                double PTE = Integer.parseInt(confArr[0].split("=")[1]);
                if(PTE < 0 || PTE > 1)
                    throw new EEException("XML error - PTE must be 0 - 1");
                res = new Tournament(elitism, conf, PTE);
                break;
            default:
                throw new EEException("XML error - Selection type " + selection.getType() + " not supported");
        }
        return res;
    }
}
