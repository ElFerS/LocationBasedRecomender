package org.recommender101;

import org.recommender101.Location.Frequency;
import org.recommender101.data.DataModel;
import org.recommender101.eval.impl.LocationBasedRecommenderImpl;
import org.recommender101.eval.interfaces.EvaluationResult;
import org.recommender101.eval.interfaces.RuntimeResult;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LocationBasedRecommender {

    LocationBasedRecommenderImpl impl;

    // Default location of property file
    public static String CONFIGURATION_FILE_G = "conf/LocationBasedRecommenderG.properties";
    public static String CONFIGURATION_FILE_F = "conf/LocationBasedRecommenderF.properties";


    /**
     * A getter for the global data model
     * @return
     */
    public DataModel getDataModel() {
        return impl.getDataModel();
    }

    // =====================================================================================

    /**
     * A constructor which takes a set of properties
     * @param confFileName
     */
    @SuppressWarnings("JavadocReference")
    public LocationBasedRecommender(Properties configuration) throws Exception {
        impl = new LocationBasedRecommenderImpl(configuration);
    }

    // =====================================================================================

    /**
     * A default constructor which loads the properties from a standard location and inits things
     */
    public LocationBasedRecommender() throws Exception {
        impl = new LocationBasedRecommenderImpl();
    }

    // =====================================================================================

    /**
     * Runs the experiments whose descriptions have been loaded in the init() phase.
     * We use the CrossValidationRunner both for cross-validation and given training/test
     * splits
     * @throws Exception
     */
    public void runExperiments() throws Exception {
        impl.runExperiments();
    }
    // =====================================================================================
    /**
     * A method that computes the average value of all cross-validation results.
     * We compute the
     * @param resultsPerEvalRun
     * @return the EvaluationResult list with averaged values
     */
    public List<EvaluationResult> calcualteAverageResults(Map<Integer, List<EvaluationResult>> resultsPerEvalRun)
            throws Exception {
        return impl.calcualteAverageResults(resultsPerEvalRun);

    }
    // =====================================================================================

    /**
     * Test only to print the evaluation results.
     * To be extended to use the algorithm classes themselves and a more detailed result presentation
     * @param results
     */
    public void printExperimentResults(List<EvaluationResult> results) {
        impl.printExperimentResults(results);
    }

    // =====================================================================================

    /**
     * Print results of evaluation arranged per metric in descending order
     * @param results
     */
    public void printSortedEvaluationResults(List<EvaluationResult> results) {
        impl.printSortedEvaluationResults(results);

        System.out.println("Overall time in seconds: "  + (impl.runner.computationTime / (double) 1000));
    }

    /**
     * Prints the runtimes of the algorithms
     * @param runtimeResult
     */
    public void printRuntimeResults(List<RuntimeResult> runtimeResult) {
        impl.printRuntimeResults(runtimeResult);
    }

    // =====================================================================================

    /**
     * Returns the results of the last experiment run
     * @return
     */
    public Map<Integer, List<EvaluationResult>> getLastDetailedResults() {
        return impl.getLastDetailedResults();
    }

    // =====================================================================================

    /**
     * Returns the averaged results
     * @return
     */
    public List<EvaluationResult> getLastResults() {
        return impl.getLastResults();
    }

    /**
     * Returns the runtime results
     * @return
     */
    public List<RuntimeResult> getLastRuntimes() {
        return impl.getLastRuntimes();
    }


    // =====================================================================================
    /**
     * A test method which runs the default configuration file
     * @param args no args accepted
     */
    public static void main(String[] args) {
        try {

            System.out.println("Generating frecuency aproach");
            Frequency fl = new Frequency();
            fl.generateRatings();

            LocationBasedRecommenderImpl rLocation;

            System.out.println("Starting evaluation with default configuration file");

            // Create the recommender
            rLocation = new LocationBasedRecommenderImpl(CONFIGURATION_FILE_F);
            //seteo los maximos y minimos que no estan hardcodeados
            rLocation.setMinRating(fl.getMinRating());
            rLocation.setMaxRating(fl.getMaxRating());
            // Initialize the recommender
            rLocation.init();

            /*System.out.println("Generating graph aproach");
            Graph gl = new Graph();
            gl.generateRatings();

            LocationBasedRecommenderImpl rLocation;

            System.out.println("Starting evaluation with default configuration file");

            // Create the recommender
            rLocation = new LocationBasedRecommenderImpl(CONFIGURATION_FILE_G);
            //seteo los maximos y minimos que no estan hardcodeados
            rLocation.setMinRating(gl.getMinRating());
            rLocation.setMaxRating(gl.getMaxRating());
            // Initialize the recommender
            rLocation.init();*/

            // Start the experiments
            rLocation.runExperiments();

            // Show results
            List<EvaluationResult> finalResult = rLocation.getLastResults();
            rLocation.printSortedEvaluationResults(finalResult);

            // Show runtimes
            List<RuntimeResult> runtimeResult = rLocation.getLastRuntimes();
            rLocation.printRuntimeResults(runtimeResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nProgram ended");
    }


    /**
     * Returns true if a certain parameter is given with a "--" prefix
     * @param args the arguments to the main class
     * @param thearg the arg we search for (without "--")
     * @return true if the argument exists
     */
    public static boolean cmdLineArgumentExists(String[] args, String thearg) {
        boolean result = false;
        for (String arg : args) {
            if (arg.startsWith("--") && arg.length() > 2) {
                if (((String) arg.subSequence(2, arg.length())).equalsIgnoreCase(thearg)) {
                    return true;
                }
            }
        }
        return result;
    }
}
