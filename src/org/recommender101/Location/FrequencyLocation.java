package org.recommender101.Location;

import org.recommender101.tools.Debug;
import org.recommender101.tools.Utilities101;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FrequencyLocation {

    protected List<Integer> usersIDs;
    protected List<Integer> venuesIDs;

    //del tipo user checkin
    protected String filename = "data/foursquare/dataset_ubicomp2013_checkins.txt";

    protected static int maxLines = -1;
    protected int counter = 0;
    // Read the file line by line and add the ratings to the data model.
    protected BufferedReader reader;
    protected String line;
    protected String separatorString = "\t";

    public FrequencyLocation() {
        usersIDs = new ArrayList<Integer>();
        venuesIDs = new ArrayList<Integer>();
    }
    public void generateRatings() throws IOException {
        Map<String,Integer> ratings= new HashMap<String, Integer>();//TODO: contemplar que no se vaya de rango el ineteger con iduser+IdVenue
        reader = new BufferedReader(new FileReader(filename));
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
            if(!usersIDs.contains(Integer.parseInt(tokens[0])))
                usersIDs.add(Integer.parseInt(tokens[0]));
            //si el lugar no esta en la lista de lugares lo agrego
            if (!venuesIDs.contains(Integer.parseInt(tokens[1])))
                venuesIDs.add(Integer.parseInt(tokens[1]));

            //key: idUser+Venue
            //int key = Integer.parseInt(tokens[0])+Integer.parseInt(tokens[1]);
            String key = tokens[0]+separatorString+tokens[1];

            if(ratings.containsKey(key)){
                //extraer el valor del map y sumarle uno, volver a agregarlo eliminando el que estaba
                int ratt = ratings.get(key).intValue() + 1;
                ratings.replace(key,ratt);
            }
            else ratings.put(key,1);
            
            line = reader.readLine();
            counter++;
            //			// debugging here..
            if (maxLines != -1) {
                if (counter >= maxLines) {
                    System.out.println("DataLoader: Stopping after " + (counter)  + " lines for debug");
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
    }
    /**
     * Copies a stream
     * @param in
     * @param out
     * @throws IOException
     */
    public static final void copyInputStream(InputStream in, OutputStream out)//para saber como joraca copiar
            throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }
    static String TARGET_FILE = "MovieLens100kRatings.txt";
    static String TARGET_DIRECTORY = "data/movielens/";
    static String TEMP_FILE = "ml-100k.zip";
    /**
     * Extracts a given file name from a zip file and deletes the zip file afterwards
     * @param targetDirectory the directory where the file is
     * @param sourceFile the zip file
     * @param fileToExtract the file to extract (has to be specified with fullly qualified name including subdirectories)
     * @param extractedFilename the target name
     * @return true if the extraction was sucessful
     */
    public static boolean extractFileFromZip(String targetDirectory, String sourceFile, String fileToExtract, String extractedFilename) {
        try {//tiena toda la signatura para moverse con el archivo y generar el nuevo archivo
            // Unzip the file
            @SuppressWarnings("resource")
            ZipFile zipFile = new ZipFile(targetDirectory + sourceFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.getName().equals(fileToExtract)) {
//						System.out.println("Found entry: " + entry.getName());
                    // create the output file
                    Utilities101.copyInputStream(zipFile.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(targetDirectory + extractedFilename)));
                    System.out.println("Created input file " + extractedFilename);
                    break;
                }
            }
            // delete unnecessary stuff
            File zipfile = new File(targetDirectory + sourceFile);
            if (zipfile.exists()) {
                zipfile.delete();
            }

            return true;
        }
        catch (Exception e) {
            System.out.println("Could not extract file "  + fileToExtract + " from " + sourceFile);
            return false;
        }
    }
}
