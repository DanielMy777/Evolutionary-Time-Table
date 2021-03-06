package Proporties.CrossoverMethods;

import Management.EEException;
import Proporties.Crossover;
import Proporties.Selection;
import Proporties.SelectionMethods.RouletteWheel;
import Proporties.SelectionMethods.Tournament;
import Proporties.SelectionMethods.Truncation;
import generated.ETTCrossover;

//This utility class can load a crossover method from a ETTCrossover object generated by unmarshalling.

public class CrossoverLoader {
    public static Crossover LoadCrossoverFromXML(ETTCrossover crossover) throws EEException
    {
        Crossover res = null;
        int cuttingPoints = crossover.getCuttingPoints();
        String[] confArr = new String[3];
        String conf = crossover.getConfiguration();
        if(conf == null)
            conf = "";
        else
            confArr = conf.split(",");
        switch (crossover.getName())
        {
            case "DayTimeOriented":
                res = new DayTimeOriented(cuttingPoints, conf);
                break;
            case "AspectOriented":
                String ori = confArr[0].split("=")[1];
                if(!ori.equals("CLASS") && !ori.equals("TEACHER"))
                    throw new EEException("XML error - unrecognizable orientation - " + ori);
                res = new AspectOriented(cuttingPoints, conf, ori);
                break;
            default:
                throw new EEException("XML error - Crossover type " + crossover.getName() + " not supported");
        }
        return res;
    }
}
