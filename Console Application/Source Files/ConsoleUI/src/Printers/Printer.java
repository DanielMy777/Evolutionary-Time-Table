package Printers;

import Database.DTO;

//A class that has print methods of a certain solution type

abstract public class Printer {
    //Print system configuration
    abstract public void PrintProperties(DTO data);
    //Print solution
    abstract public void PrintSolution(DTO data);
}
