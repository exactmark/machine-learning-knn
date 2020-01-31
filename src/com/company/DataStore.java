package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The DataStore class holds two list of DataPoints.  The original_data_store list the
 * set of datapoints before any normalization.  The normalized_data_store is the set of
 * datapoints after normalization.  The min, max, and range per attributes are used
 * to normalize new data points using the same set of attributes.
 *
 */

public class DataStore {

    private String file_name;

    public int getNum_attributes() {
        return num_attributes;
    }

    private int num_attributes;
    public List<DataPoint> normalized_data_store = new ArrayList<>();
    private List<DataPoint> original_data_store = new ArrayList<>();

    private double[] min_per_attr;
    private double[] max_per_attr;
    private double[] range_per_attr;


    //TODO we'll want to change num attributes into a list of datatypes for the bayes portion.
    //TODO cont'd: ideally this will be a char[] of "D"ecimal or "C"ategorical? With maybe a Categorical "H"ierarchical as well
    public DataStore(String file_name, int num_attributes) {
        this.file_name = file_name;
        this.num_attributes = num_attributes;

        assert num_attributes > 0;
        read_data();

        assert this.original_data_store.size() > 0;
        normalize_data();
    }

    private DataStore() {

    }

    /**
     * Given a list of integers, returns a DataStore containing only the indexed points referenced by sub_list
     * @param sub_list - a list of datapoints to retrieve.
     * @return A DataStore object containing the normalized datapoints and info regarding normalization.
     */
    public DataStore GetSubCopy(List<Integer> sub_list) {
        DataStore ReturnStore = new DataStore();
        ReturnStore.num_attributes = this.num_attributes;
        ReturnStore.min_per_attr = this.min_per_attr;
        ReturnStore.max_per_attr = this.max_per_attr;
        ReturnStore.range_per_attr = this.range_per_attr;

        for (Integer single_index :
                sub_list) {
            ReturnStore.normalized_data_store.add(this.normalized_data_store.get(single_index));
        }

        return ReturnStore;
    }


    /**
     * Populates the max min and range per attribute variables for this DataStore to be used
     * for normalization.
     */
    private void get_max_min_and_range_per_attr() {
        //Set max and min as the first.
        for (int i = 0; i < this.num_attributes; i++) {
            this.max_per_attr[i] = this.original_data_store.get(0).attributes[i];
            this.min_per_attr[i] = this.original_data_store.get(0).attributes[i];
        }

        //then find real max and min of all datapoints
        for (DataPoint thisPoint :
                this.original_data_store) {
            for (int i = 0; i < this.num_attributes; i++) {
                if (thisPoint.attributes[i] < this.min_per_attr[i]) {
                    this.min_per_attr[i] = thisPoint.attributes[i];
                }
                if (thisPoint.attributes[i] > this.max_per_attr[i]) {
                    this.max_per_attr[i] = thisPoint.attributes[i];
                }
            }
        }

        //And the range per attribute
        for (int i = 0; i < this.num_attributes; i++) {
            this.range_per_attr[i] = this.max_per_attr[i] - this.min_per_attr[i];
        }
    }

    /**
     * Populates the normalization variables, then creates the normalized data store.
     */
    private void normalize_data() {
        // init normalization lists
        this.min_per_attr = new double[this.num_attributes];
        this.max_per_attr = new double[this.num_attributes];
        this.range_per_attr = new double[this.num_attributes];

        get_max_min_and_range_per_attr();

        for (DataPoint origPoint :
                this.original_data_store) {
            DataPoint new_normalized_point = DataPoint.makeKnownDataPoint(get_normalized_attributes(origPoint.attributes),
                    origPoint.known_point_class);
            new_normalized_point.normalized = true;
            this.normalized_data_store.add(new_normalized_point);
        }

    }

    /**
     * Given a datapoint, create a normalized version and flag as normalized.
     * @param origPoint The point to be normalized.
     * @return A normalized Point
     */
    public DataPoint make_normalized_datapoint(DataPoint origPoint) {
        if (origPoint.normalized) {
            return origPoint;
        } else {
            DataPoint new_normalized_point = DataPoint.makeKnownDataPoint(get_normalized_attributes(origPoint.attributes),
                    origPoint.known_point_class);
            new_normalized_point.normalized = true;
            return new_normalized_point;
        }
    }

    /**
     * Given an array of values, returns the normalized values based on the min and range_per_attr for this DataStore
     * @param input_range A set of attributes to be normalized.
     * @return The normalized attributes.
     */
    private double[] get_normalized_attributes(double[] input_range) {
        double[] outputarray = new double[this.num_attributes];

        for (int i = 0; i < this.num_attributes; i++) {
            outputarray[i] = (input_range[i] - this.min_per_attr[i]) / this.range_per_attr[i];
        }

        return outputarray;
    }

    /**
     * Initial read function for the DataStore.  Reads the data.
     */
    protected void read_data() {
        try {
            Scanner scanner = new Scanner(new File(this.file_name));
            while (scanner.hasNext()) {
                String input_string = scanner.next();
                this.original_data_store.add(DataPoint.makeKnownDataPoint(input_string, this.num_attributes));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int get_number_of_entries() {
        return this.normalized_data_store.size();
    }
}

