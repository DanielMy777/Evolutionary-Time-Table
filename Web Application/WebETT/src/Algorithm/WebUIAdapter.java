package Algorithm;

import javafx.application.Platform;

import java.util.function.Consumer;

public class WebUIAdapter {
    private Consumer<Integer> updateGeneration;
    private Consumer<Float> updateFitness;
    private WebAlgorithemProgress updateProgress;

    public WebUIAdapter(Consumer<Integer> GenerationDelegate, Consumer<Float> FitnessDelegate, WebAlgorithemProgress ap)
    {
        updateGeneration = GenerationDelegate;
        updateFitness = FitnessDelegate;
        updateProgress = ap;
    }

    public void updateEnteringNewGeneration(int gen)
    {
        Platform.runLater(() ->
        {
            updateGeneration.accept(gen);
            updateProgress.setCurrentGeneration(gen);
        });

    }

    public void updateTimeElapsed(long secs)
    {
        Platform.runLater(() -> {
            updateProgress.setCurrentTime(secs);
        });

    }

    public void updateNewBestFitness(float bestFitness)
    {
        Platform.runLater(() -> {
            updateFitness.accept(bestFitness);
            updateProgress.setCurrentFitness(bestFitness);
        });

    }
}
