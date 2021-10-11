package Controls;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

public class AlgorithemProgress {
    public static final int NO_PREFERENCE = -1;
    private double maxGenerations;
    private double maxFitness;
    private double maxDuration;
    private SimpleIntegerProperty currentGeneration;
    private SimpleFloatProperty currentFitness;
    private SimpleLongProperty currentTime;
    private SimpleDoubleProperty GenerationsProgress;
    private SimpleDoubleProperty FitnessProgress;
    private SimpleDoubleProperty DurationsProgress;

    public AlgorithemProgress(int generations, float fitness, long duration)
    {
        maxGenerations = generations;
        maxFitness = fitness;
        maxDuration = duration;
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

    public double getGenerationsProgress() {
        return GenerationsProgress.get();
    }

    public SimpleDoubleProperty generationsProgressProperty() {
        return maxGenerations == NO_PREFERENCE ? new SimpleDoubleProperty(0) : GenerationsProgress;
    }

    public double getFitnessProgress() {
        return FitnessProgress.get();
    }

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
    }

    public void setCurrentFitness(float currentFitness) {
        if(maxFitness != NO_PREFERENCE)
            this.currentFitness.set((float) Math.min(currentFitness, maxFitness));
        else
            this.currentFitness.set(currentFitness);
    }

    public void setCurrentTime(long currentTime) {
        if(maxDuration != NO_PREFERENCE)
            this.currentTime.set((long) Math.min(currentTime, maxDuration));
        else
            this.currentTime.set(currentTime);
    }

    public boolean reachedEndStatement()
    {
        return (getGenerationsProgress() >= 1 || getFitnessProgress() >= 1 || getDurationsProgress() >= 1);
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
