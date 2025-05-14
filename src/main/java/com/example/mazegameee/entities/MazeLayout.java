package com.example.mazegameee.entities;

import java.io.*;
import java.util.*;

public class MazeLayout {
    private final boolean[][] eastOpen;
    private final boolean[][] southOpen;

    private MazeLayout(int rows, int cols) {
        eastOpen  = new boolean[rows][cols];
        southOpen = new boolean[rows][cols];
    }

    public static MazeLayout loadFromCSV(String resourcePath, int rows, int cols) throws IOException {
        MazeLayout layout = new MazeLayout(rows, cols);
        try (InputStream is = MazeLayout.class.getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int r = 0;
            while ((line = br.readLine()) != null && r < rows) {
                String[] parts = line.split(",");
                for (int c = 0; c < Math.min(parts.length, cols); c++) {
                    int code = Integer.parseInt(parts[c].trim());
                    layout.eastOpen [r][c] = (code & 2) != 0;
                    layout.southOpen[r][c] = (code & 4) != 0;
                }
                r++;
            }
        }
        return layout;
    }

    public boolean hasEastOpening (int r, int c) { return eastOpen [r][c]; }
    public boolean hasSouthOpening(int r, int c) { return southOpen[r][c]; }
}
