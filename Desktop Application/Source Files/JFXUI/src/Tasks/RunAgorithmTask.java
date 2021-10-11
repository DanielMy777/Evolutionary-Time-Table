package Tasks;

import Controls.UIAdapter;
import Data.Solution;
import Data.SolutionFactory;
import Database.EvoAlgorithem;
import Database.TTFactory;
import TTSolution.FilledTimeTable;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.concurrent.Task;
import org.omg.CORBA.portable.Delegate;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RunAgorithmTask extends Task<Boolean> {

    private EvoAlgorithem<FilledTimeTable> theAlg;
    private Consumer<Solution> saveSolutionOfGen;
    private Runnable onCancel;
    private Supplier<Boolean> toStop;
    private Supplier<Boolean> toPause;
    private UIAdapter uiAdapter;
    private int maxGenerations;


    public RunAgorithmTask(EvoAlgorithem alg, Consumer<Solution> saveSolution,Supplier<Boolean> Stop, Supplier<Boolean> Pause, UIAdapter ui)
    {
        theAlg = alg;
        saveSolutionOfGen = saveSolution;
        toStop = Stop;
        toPause = Pause;
        uiAdapter = ui;
    }

    public void setOnCancel(Runnable oc)
    {
        onCancel = oc;
    }

    public void setToPause(Supplier<Boolean> tp)
    {
        toPause = tp;
    }

    public void setMaxGenerations(int val)
    {
        maxGenerations = val;
    }

    @Override
    protected Boolean call() throws Exception {

        int currentGen = 0;
        double bestFit = 0;
        Solution currBestSolution;
        double currentBestFit = 0;
        theAlg.generateInitialPopulation(new TTFactory());
        Instant startTime = Instant.now();
        Instant endTime;
        Instant pauseStart, pauseEnd;
        long pauseSecs = 0;
        saveSolutionOfGen.accept(theAlg.sortAndFindBestSolution());
        bestFit = theAlg.findFitnessOfBestSolution();
        uiAdapter.updateEnteringNewGeneration(currentGen);
        uiAdapter.updateNewBestFitness((float)bestFit);

        updateMessage("Process Started!");
        try {
            while (!toStop.get() && !Thread.currentThread().isInterrupted()) {
                synchronized (theAlg) {
                    pauseStart = Instant.now();
                    while (toPause.get())
                        theAlg.wait();
                    if (toStop.get())
                        throw new TaskIsCanceledException();
                    pauseEnd = Instant.now();
                    pauseSecs += pauseEnd.getEpochSecond() - pauseStart.getEpochSecond();
                }
                if(currentGen >= maxGenerations)
                    throw new TaskIsCanceledException();
                theAlg.createNextGeneration();
                currentGen++;
                uiAdapter.updateEnteringNewGeneration(currentGen);
                currBestSolution = theAlg.sortAndFindBestSolution();
                currentBestFit = currBestSolution.getFitness();
                saveSolutionOfGen.accept(currBestSolution);
                if (bestFit < currentBestFit) {
                    bestFit = currentBestFit;
                    uiAdapter.updateNewBestFitness((float) currentBestFit);
                }
                endTime = Instant.now();
                uiAdapter.updateTimeElapsed(endTime.getEpochSecond() - startTime.getEpochSecond() - pauseSecs);
            }
        }catch (TaskIsCanceledException ignored) {}

        onCancel.run();
        updateMessage("Process Finished!");
        done();
        return true;
    }
}
