package org.recommender101.Location;

import org.recommender101.data.Rating;
import org.recommender101.tools.Debug;

import java.io.*;
import java.util.*;

public class Frequency extends LocationRatingGenerator{

    protected Map<String, Integer> ratings = new HashMap<String, Integer>();

    @Override
    protected void doRank(int usr, int vne) {
        String key = Integer.toString(usr)+separatorString+Integer.toString(vne);
        if (ratings.containsKey(key)) {
            //extraer el valor del map y sumarle uno, volver a agregarlo eliminando el que estaba
            int rat = ratings.get(key) + 1;
            ratings.replace(key, rat);

            if (rat>MAX_RATING)
                MAX_RATING = rat;

            if (rat<MIN_RATING)
                MIN_RATING = rat;
        } else
            ratings.put(key, 1);
    }

    @Override
    protected List<Rating> getRatings() {
        List<Rating> ratings1 = new ArrayList<Rating>();
        for (int user : usersIDs)
            for (int venue : venuesIDs) {
                // debugg here
                String composedKey = Integer.toString(user) + separatorString + Integer.toString(venue);
                if (ratings.containsKey(composedKey))
                    ratings1.add(new Rating(user,venue,ratings.get(composedKey)));
            }
        return ratings1;
    }

    public void generateRatingss() throws IOException {

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

            //key: idUser+Venue
            //int key = Integer.parseInt(tokens[0])+Integer.parseInt(tokens[1]);
            String key = tokens[0] + separatorString + tokens[1];
            //doRank
            if (ratings.containsKey(key)) {
                //extraer el valor del map y sumarle uno, volver a agregarlo eliminando el que estaba
                int ratt = ratings.get(key) + 1;
                ratings.replace(key, ratt);
                if (ratt>MAX_RATING)
                    MAX_RATING = ratt;
                if (ratt<MIN_RATING)
                    MIN_RATING = ratt;
            } else ratings.put(key, 1);
            //end doRank

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
        /*termino de leer el archivo, ahora recorro la lista de usuarios y venues, busco el par y agrego el valor del
        contador, si no tiene valor (no existe la key) agrego un cero
         */
        out = new BufferedOutputStream(new FileOutputStream(TARGET_DIRECTORY + TARGET_FILE));
        for (int user : usersIDs)
            for (int venue : venuesIDs) {
                // debugg here
                String composedKey = user + separatorString + venue;
                if (ratings.containsKey(composedKey))
                    out.write(Integer.parseInt(composedKey + separatorString + ratings.get(composedKey)));
                else
                    out.write(Integer.parseInt(user + separatorString + venue + separatorString + 0));

            }
        System.out.println("Created input file " + TARGET_FILE);
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
