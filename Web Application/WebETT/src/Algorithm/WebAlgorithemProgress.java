package Algorithm;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class WebAlgorithemProgress implements Serializable {
    public static final int NO_PREFERENCE = -1;
    private double maxGenerations;
    private double maxFitness;
    private double maxDuration;
    private int currGeneration;
    private float currFitness;
    private long currTime;

    Map<Integer, Double> bestFitnessByGeneration;
    private int ratio = 1;


    private transient SimpleIntegerProperty currentGeneration;
    private transient SimpleFloatProperty currentFitness;
    private transient SimpleLongProperty currentTime;
    private transient SimpleDoubleProperty GenerationsProgress;
    private transient SimpleDoubleProperty FitnessProgress;
    private transient SimpleDoubleProperty DurationsProgress;

    public WebAlgorithemProgress(int generations, float fitness, long duration)
    {
        maxGenerations = generations;
        maxFitness = fitness;
        maxDuration = duration;
        bestFitnessByGeneration = new TreeMap<>();
        bestFitnessByGeneration.put(0,0d);
        currentGeneration = new SimpleIntegerProperty(0);
        currentFitness = new SimpleFloatProperty(0);
        currentTime = new SimpleLongProperty(0);
        GenerationsProgress = new SimpleDoubleProperty(0);
        FitnessProgress = new SimpleDoubleProperty(0);
        DurationsProgress = new SimpleDoubleProperty(0);
        GenerationsProgress.bind(Bindings.divide(currentGeneration, maxGenerations));
        FitnessProgress.bind(Bindings.divide(currentFitness, maxFitness));
        DurationsProgress.bind(Bindings.divide(currentTime, maxDuration));
    }

    public synchronized Map<Integer, Double> getBestFitnessByGeneration() {
        return bestFitnessByGeneration;
    }

    public synchronized void addFitnessToChart(double fitness)
    {
        if(bestFitnessByGeneration.size() >= 100)
        {
            ratio *= 10;
            bestFitnessByGeneration.keySet().removeIf(key -> (key % ratio) != 0);
        }
        int gen = getCurrentGeneration();
        if(gen % ratio == 0)
            bestFitnessByGeneration.put(gen, fitness);
    }

    public double getGenerationsProgress() {
        return GenerationsProgress.get();
    }

    public SimpleDoubleProperty generationsProgressProperty() {
        return maxGenerations == NO_PREFERENCE ? new SimpleDoubleProperty(0) : GenerationsProgress;
    }

    public double getFitnessProgress() {
        return FitnessProgress.get();
    }

    public float getCurrentFitness() {return currentFitness.get(); }
    public int getCurrentGeneration() {return currentGeneration.get(); }
    public int getMaxGeneration() {return maxGenerations == NO_PREFERENCE ? Integer.MAX_VALUE : (int)maxGenerations;}

    public SimpleDoubleProperty fitnessProgressProperty() {
        return maxFitness == NO_PREFERENCE ? new SimpleDoubleProperty(0) : FitnessProgress;
    }

    public double getDurationsProgress() {
        return DurationsProgress.get();
    }

    public SimpleDoubleProperty durationsProgressProperty() {
        return maxDuration == NO_PREFERENCE ? new SimpleDoubleProperty(0) : DurationsProgress;
    }

    public void setCurrentGeneration(int currentGeneration) {
        if(maxGenerations != NO_PREFERENCE)
            this.currentGeneration.set((int) Math.min(currentGeneration, maxGenerations));
        else
            this.currentGeneration.set(currentGeneration);
        currGeneration = this.currentGeneration.get();
    }

    public void setCurrentFitness(float currentFitness) {
        if(maxFitness != NO_PREFERENCE)
            this.currentFitness.set((float) Math.min(currentFitness, maxFitness));
        else
            this.currentFitness.set(currentFitness);
        currFitness = this.currentFitness.get();
    }

    public void setCurrentTime(long currentTime) {
        if(maxDuration != NO_PREFERENCE)
            this.currentTime.set((long) Math.min(currentTime, maxDuration));
        else
            this.currentTime.set(currentTime);
        currTime = this.currentTime.get();
    }

    public boolean reachedEndStatement()
    {
        return (getGenerationsProgress() >= 1 || getFitnessProgress() >= 1 || getDurationsProgress() >= 1);
    }

    public void clearPartialProgress()
    {
        currGeneration = 0;
        currFitness = 0;
        currTime = 0;

        currentFitness.set(0);
        currentGeneration.set(0);
        currentTime.set(0);

        bestFitnessByGeneration.clear();
        bestFitnessByGeneration.put(0, 0d);
        ratio = 1;
    }

    public void clearAllProgress()
    {
        Platform.runLater(() -> {
            currentFitness.set(0);
            currentGeneration.set(0);
            currentTime.set(0);
        });
    }
}
