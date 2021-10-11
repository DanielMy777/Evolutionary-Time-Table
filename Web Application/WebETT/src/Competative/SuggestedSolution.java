package Competative;

import Algorithm.RunWebAgorithmTask;
import Algorithm.WebAlgorithemProgress;
import Algorithm.WebUIAdapter;
import Data.Solution;
import Database.EvoAlgorithem;
import Database.SolutionLoader;
import Main.EEException;
import TTSolution.FilledTimeTable;
import Users.User;

import java.util.Map;
import java.util.TreeMap;

public class SuggestedSolution {

    User owner;
    EvoAlgorithem<FilledTimeTable> preferences;
    SolutionLoader allData;
    Solution bestSolution;
    WebAlgorithemProgress currentProgress;
    transient RunWebAgorithmTask currentTask;
    transient Thread algorithemTaskThread;

    boolean running;
    boolean paused;

    long currentGen;
    long currentBestFit;
    long currentTime;

    public SuggestedSolution(User user, IssuedProblem theProb) throws EEException {
        owner = user;
        preferences = new EvoAlgorithem<>(theProb.getProblemDefinition().getAllRules(), false);
        allData = theProb.getProblemDefinition();
        bestSolution = null;
        currentProgress = null;
        currentTask = null;
        algorithemTaskThread = null;

        running = false;
        paused = false;

        currentGen = 0;
        currentBestFit = 0;
        currentTime = 0;
    }

    public EvoAlgorithem<FilledTimeTable> getPreferences() {
        return preferences;
    }

    public void setPreferences(EvoAlgorithem<FilledTimeTable> preferences) {
        this.preferences = preferences;
    }

    public synchronized Solution getBestSolution() {
        return bestSolution;
    }

    public synchronized void setBestSolution(Solution bestSolution) {
        this.bestSolution = bestSolution;
    }

    public WebAlgorithemProgress getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(WebAlgorithemProgress currentProgress) {
        this.currentProgress = currentProgress;
    }

    public long getCurrentGen() {
        return currentGen;
    }

    public void setCurrentGen(long currentGen) {
        this.currentGen = currentGen;
    }

    public long getCurrentBestFit() {
        return currentBestFit;
    }

    public void setCurrentBestFit(long currentBestFit) {
        this.currentBestFit = currentBestFit;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public RunWebAgorithmTask getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(RunWebAgorithmTask currentTask) {
        this.currentTask = currentTask;
    }

    public Thread getAlgorithemTaskThread() {
        return algorithemTaskThread;
    }

    public void setAlgorithemTaskThread(Thread algorithemTaskThread) {
        this.algorithemTaskThread = algorithemTaskThread;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void playSolution() throws EEException {
        if (!this.paused) {
            clearSolution();
            currentProgress.clearPartialProgress();
            setCurrentTask(new RunWebAgorithmTask(
                    preferences,
                    this::setBestSolution,
                    currentProgress::addFitnessToChart,
                    currentProgress::reachedEndStatement,
                    this::isPaused,
                    currentProgress
            ));
            currentTask.setAlgData(allData);
            currentTask.setOnCancel(() -> {
                this.running = false;
                this.paused = false;
                algorithemTaskThread = null;
            });
            currentTask.setMaxGenerations(currentProgress.getMaxGeneration());
            algorithemTaskThread = new Thread(currentTask);
            algorithemTaskThread.start();
            this.running = true;
            this.paused = false;
        } else //unpause
        {
            synchronized (preferences) {
                this.running = true;
                this.paused = false;
                preferences.notify();
            }
        }
    }

    public void pauseSolution()
    {
        this.paused = true;
        this.running = false;
    }

    public void stopSolution()
    {
        if(algorithemTaskThread != null) {
            algorithemTaskThread.interrupt();
            algorithemTaskThread = null;
        }

    }

    public void clearSolution() throws EEException {
        preferences.clearProgress();
        bestSolution = null;
        currentGen = 0;
        currentBestFit = 0;
        currentTime = 0;
    }
}
