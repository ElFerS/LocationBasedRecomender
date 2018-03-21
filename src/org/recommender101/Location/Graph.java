package org.recommender101.Location;

import org.recommender101.data.Rating;
import org.recommender101.tools.Debug;

import java.io.*;
import java.util.*;

public class Graph extends LocationRatingGenerator{

    protected Map<Integer, ArrayList<Integer>> graphLocations = new HashMap<Integer, ArrayList<Integer>>();


    @Override
    protected void doRank(int usr, int vne) {
        ArrayList<Integer> graph = graphLocations.get(usr);
        if (graph != null){//no key, then null
            graph.add(vne);
            graphLocations.replace(usr,graph);
        }
        else graphLocations.put(usr,new ArrayList<Integer>(vne));
    }

    @Override
    protected List<Rating> getRatings() {
        List<Rating> ratings1 = new ArrayList<Rating>();
        for (int usrID : graphLocations.keySet())
        {
            ArrayList<Integer> graph = graphLocations.get(usrID);
            //loop venue del grafo de recorrido del usuario usrID
            for (int vne = 0; vne < graph.size(); vne++ ){
                int cicle = getCicle(graphLocations.get(usrID), vne, graph.get(vne));
                if (cicle != -1){
                    ArrayList<Integer> sub = (ArrayList<Integer>) graph.subList(vne,cicle);
                    //USER\tVENUE\tRATING
                    //String lineToWrite = usrID + separatorString + graph.get(vne) + separatorString + sub.size();
                    ratings1.add(new Rating(usrID,graph.get(vne),sub.size()));
                }
            }
        }
        return normalice(ratings1);
    }

    private List<Rating> normalice(List<Rating> ratings){

        return null;
    }

    public void generateRatingss() throws IOException{

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

            Integer user = Integer.parseInt(tokens[0]);
            Integer venue = Integer.parseInt(tokens[1]);

            //si el usuario no esta en la lista de usuarios lo agrego
            if (!usersIDs.contains(user))
                usersIDs.add(user);
            //si el lugar no esta en la lista de lugares lo agrego
            if (!venuesIDs.contains(venue))
                venuesIDs.add(venue);

            //doRank
            ArrayList<Integer> graph = graphLocations.get(user);
            if (graph != null){//no key, then null
                graph.add(venue);
                graphLocations.replace(user,graph);
            }
            else graphLocations.put(user,new ArrayList<Integer>(venue));
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
        /* archivo leido, se tienen los usuarios, las venues y el hashmap con usario y su grafo de venues
        con esta ultima estructura se generan los ratings
         */

        out = new BufferedOutputStream(new FileOutputStream(TARGET_DIRECTORY + TARGET_FILE));
        //loop por usuario para obtener su grafo de venues
        for (int usrID : graphLocations.keySet())
        {
            ArrayList<Integer> graph = graphLocations.get(usrID);
            //loop venue del grafo de recorrido del usuario usrID
            for (int vne = 0; vne < graph.size(); vne++ ){
                int cicle = getCicle(graphLocations.get(usrID), vne, graph.get(vne));
                if (cicle != -1){
                    ArrayList<Integer> sub = (ArrayList<Integer>) graph.subList(vne,cicle);
                    //USER\tVENUE\tRATING
                    String lineToWrite = usrID + separatorString + graph.get(vne) + separatorString + sub.size();
                    out.write(Integer.parseInt(lineToWrite));
                }
                else {
                    //USER\tVENUE\tRATING
                    String lineToWrite = usrID + separatorString + graph.get(vne) + separatorString + 0;
                    out.write(Integer.parseInt(lineToWrite));
                }
            }
        }
        System.out.println("Created input file " + TARGET_FILE);
        try {
            //Se cierra el archivo, ahora hace falta estandarizar los valores
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * busca ciclos dentro de la lista de locaciones
     * @param graph grafo completo con el recorrido que hizo el usuario
     * @param pos posicion dentro del grafo de venues
     * @param vneID venue en si, su id
     * @return posicion donde cierra el ciclo, si no hay ciclo valor default es -1
     */
    private int getCicle(ArrayList<Integer> graph, int pos, int vneID) {
        int res = -1;
        for (int venue = pos; venue < graph.size(); venue++)
            if (graph.get(venue) == vneID)
                return venue;
        return res;
    }

}
