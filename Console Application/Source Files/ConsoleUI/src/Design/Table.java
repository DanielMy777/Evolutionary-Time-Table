package Design;

import java.util.ArrayList;
import java.util.Collections;

//This class represents a printable custom table

public class Table {

    private String tableName;
    private int cols;
    private int rows;
    private String[] headers;
    private ArrayList<String[]> info;
    private int[] maxWidthInCol;

    //Contructor, provide the table's name and the headers for the columns.
    public Table(String name, String... NewHeaders)
    {
        tableName = name;
        cols = NewHeaders.length;
        maxWidthInCol = new int[cols];
        rows = 0;
        headers = NewHeaders.clone();
        info = new ArrayList<>();
        for(int i=0; i<cols; i++)
        {
            maxWidthInCol[i] = headers[i].length();
        }
    }

    /**
     * add a row to the table
     *
     * @param NewInfo array of information, each string will be under the fitting header.
     */
    public boolean addRow(String... NewInfo)
    {
        if(cols != NewInfo.length)
            return false;
        info.add(NewInfo.clone());
        rows++;
        for(int i=0; i<cols; i++)
        {
            if(!(NewInfo[i] == null))
                maxWidthInCol[i] = Math.max(maxWidthInCol[i], NewInfo[i].length());
        }
        return true;
    }

    /**
     * Print the table to the screen
     */
    public void printTable()
    {
        System.out.println(tableName + ":");
        StringBuilder sb = new StringBuilder("");
        sb.append(String.join("", Collections.nCopies(tableName.length() + 1,".")));
        System.out.println(sb);
        printHeaders();
        for(int i=0; i<rows; i++)
        {
            printRow(i);
        }
    }

    private void printHeaders()
    {
        StringBuilder sb = new StringBuilder(" ");
        for(int i=0; i<cols; i++)
        {
            sb.append(String.join("", Collections.nCopies(maxWidthInCol[i] + 7,"=")));
        }
        sb.append("\b\n");
        for(int i=0; i<cols; i++)
        {
            sb.append("|   " + String.format("%-"+maxWidthInCol[i]+"s", headers[i]) + "   ");
        }
        sb.append("|\n ");
        for(int i=0; i<cols; i++)
        {
            sb.append(String.join("", Collections.nCopies(maxWidthInCol[i] + 7,"=")));
        }
        sb.append("\b");
        System.out.println(sb);
    }

    private void printRow(int row)
    {
        StringBuilder sb = new StringBuilder("");

        for(int i=0; i<cols; i++)
        {
            sb.append("|   " + String.format("%-"+maxWidthInCol[i]+"s", info.get(row)[i]) + "   ");
        }
        sb.append("|\n ");
        for(int i=0; i<cols; i++)
        {
            sb.append(String.join("", Collections.nCopies(maxWidthInCol[i] + 7,"-")));
        }
        sb.append("\b");
        System.out.println(sb);
    }

}
