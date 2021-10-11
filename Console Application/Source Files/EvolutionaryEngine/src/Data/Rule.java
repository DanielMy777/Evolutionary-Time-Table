package Data;

//This interface represents a rule to enforce in the evolutionary algorithm.

public interface Rule {

    /**
     * Evaluate the fitness of a solution considering this rule.
     *
     * @param sol the solution to check.
     *
     * @return The amount of endurance this solution has over this rule.
     */
    public double eval(Solution sol);
}
