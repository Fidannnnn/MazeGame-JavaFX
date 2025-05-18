package com.example.mazegameee.entities;

import java.io.*;
import java.util.*;

public class MazeLayout {
    // Each cell can have an opening to the east and/or to the south
    // We store those separately in two 2D boolean arrays
    private final boolean[][] eastOpen;
    private final boolean[][] southOpen;

    // constructor – just allocates the arrays
    private MazeLayout(int rows, int cols) {
        eastOpen  = new boolean[rows][cols];
        southOpen = new boolean[rows][cols];
    }

    // Loads the maze layout from a CSV file (from the resources folder)
    // Each number in the CSV tells us if there’s a door to the east (2), south (4), or both (6)
    public static MazeLayout loadFromCSV(String resourcePath, int rows, int cols) throws IOException {
        MazeLayout layout = new MazeLayout(rows, cols);

        // open input stream from resource path and read it line by line
        try (InputStream is = MazeLayout.class.getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int r = 0;

            // read lines one by one until we either run out or hit the row limit
            while ((line = br.readLine()) != null && r < rows) {
                String[] parts = line.split(",");

                // go through each value in the line, up to the column count
                for (int c = 0; c < Math.min(parts.length, cols); c++) {
                    int code = Integer.parseInt(parts[c].trim());

                    // if the 2 bit is set, east is open
                    layout.eastOpen[r][c] = (code & 2) != 0;

                    // if the 4 bit is set, south is open
                    layout.southOpen[r][c] = (code & 4) != 0;
                }

                // go to next row
                r++;
            }
        }

        return layout;
    }

    // basic accessors for door layout
    public boolean hasEastOpening (int r, int c) { return eastOpen [r][c]; }
    public boolean hasSouthOpening(int r, int c) { return southOpen[r][c]; }
}
