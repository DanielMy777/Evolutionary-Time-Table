package Algorithm;

import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Data.Solution;
import Database.EvoAlgorithem;
import Database.SolutionLoader;
import Database.TTFactory;
import Database.TTLoader;
import TTSolution.FilledTimeTable;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;

    public class RunWebAgorithmTask implements Runnable {

    private EvoAlgorithem<FilledTimeTable> theAlg;
    private TTLoader allData;
    private Consumer<Solution> updateBestSolution;
    private Consumer<Double> addFitnessToChart;
    private Runnable onCancel;
    private Supplier<Boolean> toStop;
    private Supplier<Boolean> toPause;
    private WebAlgorithemProgress currentProgress;
    private int maxGenerations;


    public RunWebAgorithmTask(EvoAlgorithem alg, Consumer<Solution> saveSolution, Consumer<Double> addFitnessToDB, Supplier<Boolean> Stop, Supplier<Boolean> Pause, WebAlgorithemProgress ap)
    {
        theAlg = alg;
        updateBestSolution = saveSolution;
        addFitnessToChart = addFitnessToDB;
        toStop = Stop;
        toPause = Pause;
        currentProgress = ap;
    }

    public void setOnCancel(Runnable oc)
    {
        onCancel = oc;
    }

    public void setAlgData(SolutionLoader sl) { allData = (TTLoader) sl; }

    public void setToPause(Supplier<Boolean> tp)
    {
        toPause = tp;
    }

    public void setMaxGenerations(int val)
    {
        maxGenerations = val;
    }

    @Override
    public void run() {

        int currentGen = 0;
        double bestFit = 0;
        Solution currBestSolution;
        double currentBestFit = 0;
        theAlg.generateInitialPopulation(new TTFactory(allData));
        Instant startTime = Instant.now();
        Instant endTime;
        Instant pauseStart, pauseEnd;
        long pauseSecs = 0;
        updateBestSolution.accept(theAlg.sortAndFindBestSolution());
        try{
            bestFit = theAlg.findFitnessOfBestSolution();
        }
        catch (Exception e) {}
        currentProgress.setCurrentGeneration(currentGen);
        currentProgress.setCurrentFitness((float) bestFit);

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
                currentProgress.setCurrentGeneration(currentGen);
                currBestSolution = theAlg.sortAndFindBestSolution();
                try {
                    currentBestFit = currBestSolution.getFitness();
                }
                catch (Exception e) {}
                addFitnessToChart.accept(currentBestFit);
                if (bestFit < currentBestFit) {
                    bestFit = currentBestFit;
                    currentProgress.setCurrentFitness((float) currentBestFit);
                    updateBestSolution.accept(currBestSolution);
                }
                endTime = Instant.now();
                currentProgress.setCurrentTime(endTime.getEpochSecond() - startTime.getEpochSecond() - pauseSecs);
            }
        }catch (TaskIsCanceledException | InterruptedException ignored) {}


        onCancel.run();
    }
}
