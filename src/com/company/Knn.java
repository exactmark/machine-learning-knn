package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A simple KNN modeling class.
 */
public class Knn {

    /**
     * Holds a tuple of distance and point class.  Allows sorting by distance to determine
     * closest points.
     */
    class DistanceArrayPoint {

        double distance;
        String point_class;

        public DistanceArrayPoint(double distance, String point_class) {
            this.distance = distance;
            this.point_class = point_class;
        }
    }

    /**
     * Sort function for {distance,class}
     */
    class SortByDistance implements Comparator<DistanceArrayPoint> {
        public int compare(DistanceArrayPoint left, DistanceArrayPoint right) {
            return Double.compare(left.distance, right.distance);
        }
    }

    DataStore dataStore;

    List<DistanceArrayPoint> distance_list = new ArrayList<>();


    public Knn(DataStore new_store) {
        this.dataStore = new_store;
    }

    public Knn() {

    }

    /**
     * For a given test_point, populate the list of distances from this test_point
     * @param test_point the point to be classified.
     */
    private void populate_distance_list(DataPoint test_point) {
        this.distance_list.clear();
        assert test_point.normalized;

        for (DataPoint known_point :
                this.dataStore.normalized_data_store) {
            this.distance_list.add(new DistanceArrayPoint(known_point.get_distance(test_point), known_point.known_point_class));
        }

        this.distance_list.sort(new SortByDistance());

    }

    /**
     * Given a map of Strings/Integers, determine the highest value or return NULL if tied.
     * @param target_map A tally of classes/integers
     * @return The String value of the highest class or NULL
     */
    public String get_highest_map(Map<String, Integer> target_map) {

        // if nothing included in map, return null.
        // or if only one thing, return the one thing.
        // Otherwise do more expensive calculations below.
        if (target_map.size() == 0) {
            return null;
        } else if (target_map.size() == 1) {
            return target_map.keySet().iterator().next();
        }

        //Create a list of (counts,classes} and sort by counts.
        List<Integer> all_vals = new ArrayList<>();
        SortedMap<Integer, String> sorted_vals = new TreeMap<>();
        for (String this_key :
                target_map.keySet()) {
            all_vals.add(target_map.get(this_key));
            sorted_vals.put(target_map.get(this_key), this_key);
        }

        Collections.sort(all_vals);
        int all_val_length = all_vals.size();
        // if highest and second highest are equal, return null.
        if (all_vals.get(all_val_length - 1) == all_vals.get(all_val_length - 2)) {
            return null;
        }
        // else return highest count
        return sorted_vals.get(all_vals.get(all_val_length - 1));
    }

    /**
     * given a test_point, return the predicted class or NULL.
     * Assumes test point is already normalized.
     * @param test_point the test point to classify.
     * @param K Number of closest points to use for classification.
     * @return String of predicted class.
     */
    public String getKClass(DataPoint test_point, int K) {
        assert this.dataStore.get_number_of_entries() > 0;
        assert K <= this.dataStore.get_number_of_entries();

        populate_distance_list(test_point);

        Map<String, Integer> resultDict = new HashMap<>();

        //Tally the closest K classes by class.
        for (int i = 0; i < K; i++) {
            String found_class = this.distance_list.get(i).point_class;
            resultDict.merge(found_class, 1, Integer::sum);
        }

        //Determine the highest tallied class or NULL.
        String ReturnClass = get_highest_map(resultDict);

        return ReturnClass;
    }

    /**
     * Assumed most likely use case. Given that training set is already populated, will
     * read a file and classify all points therein.
     * @param test_file Path to file to classify.
     * @param k_value K points to classify by.
     */
    public void classify_file(String test_file, int k_value) {

        try {
            Scanner scanner = new Scanner(new File(test_file));
            while (scanner.hasNext()) {
                String input_string = scanner.next();
                DataPoint unknown_point = DataPoint.makeUnknownDataPoint(input_string, dataStore.getNum_attributes());
                unknown_point = dataStore.make_normalized_datapoint(unknown_point);
                String predicted_class = this.getKClass(unknown_point, k_value);
                if (predicted_class==null){
                    predicted_class="Undetermined";
                }
                System.out.printf("%s predicted: %s\n", input_string, predicted_class);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Runs k classification on all points in a datastore.  Assumes both datastores
     * are normalized using the same normalization parameters.
     * @param test datastore to be classified.
     * @param k K-value for classification.
     */
    public void classify_datastore(DataStore test, int k) {
        for (DataPoint single_point :
                test.normalized_data_store) {
            single_point.predicted_point_class = this.getKClass(single_point, k);
        }
    }

}
