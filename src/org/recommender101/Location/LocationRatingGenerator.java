package org.recommender101.Location;

import org.recommender101.data.Rating;
import org.recommender101.tools.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LocationRatingGenerator {

    protected List<Integer> usersIDs = new ArrayList<Integer>();
    protected List<Integer> venuesIDs = new ArrayList<Integer>();

    //UserID VenueID rating
    protected OutputStream out;
    protected String TARGET_FILE = "foursquare100kRatings.txt";
    protected String TARGET_DIRECTORY = "data/foursquare/";
    protected String FILE_NAME = "data/foursquare/dataset_ubicomp2013_checkins.txt";

    protected static int maxLines = -1;
    protected int counter = 0;
    protected BufferedReader reader;
    protected String line;
    protected String separatorString = "\t";

    public static float MIN_RATING = 1000;
    public static float MAX_RATING = -1;

    protected abstract void doRank(int usr, int vne);
    protected abstract List<Rating> getRatings();

    public void generateRatings() throws IOException{
        reader = new BufferedReader(new FileReader(FILE_NAME));
        line = reader.readLine();
        String[] tokens;

        while (line != null) {
            // Skip comment lines
            if (line.trim().startsWith("//")) {
                line = reader.readLine();
                continue;
            }
            tokens = line.split(separatorString);

            //si el usuario no esta en la lista de usuarios lo agrego
            if (!usersIDs.contains(Integer.parseInt(tokens[0])))
                usersIDs.add(Integer.parseInt(tokens[0]));
            //si el lugar no esta en la lista de lugares lo agrego
            if (!venuesIDs.contains(Integer.parseInt(tokens[1])))
                venuesIDs.add(Integer.parseInt(tokens[1]));

            //Tokens[0]: USER, Tokens[1]: VENUE
            doRank(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]));

            line = reader.readLine();
            counter++;
            //			// debugging here..
            if (maxLines != -1) {
                if (counter >= maxLines) {
                    System.out.println("DataLoader: Stopping after " + (counter) + " lines for debug");
                    break;
                }
            }
        }
        Debug.log("LocationDataLoader:loadData: Loaded " + counter + " ratings");
        Debug.log("LocationDataLoader:loadData: " + usersIDs.size() + " usersIDs and " + venuesIDs.size() + " items.");
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ranks done, write file
        wirteFile();
    }

    protected void wirteFile()throws IOException{
        out = new BufferedOutputStream(new FileOutputStream(TARGET_DIRECTORY + TARGET_FILE));
        for (Rating r : getRatings())
            out.write(Integer.parseInt(r.toString()));

        System.out.println("Created input file " + TARGET_FILE);

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static float getMinRating() {
        return MIN_RATING;
    }

    public static float getMaxRating() {
        return MAX_RATING;
    }
}
