package Threads;

import Interactive.Menu;
import Interactive.StartAlgorithem;

public class AlgThread extends Thread{

    public void run()
    {
        try {
            StartAlgorithem.PerformEvolutionProcess();
        } catch (Exception e) {
            System.out.println(">>>>>>>>>>>>>>>>>");
            System.out.println(e.getMessage());
            System.out.println(">>>>>>>>>>>>>>>>>");;
        }
    }
}
