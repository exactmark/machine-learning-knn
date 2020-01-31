package com.company;

public class DataPoint {
    public double[] attributes;
    public String known_point_class;
    public String predicted_point_class;
    public boolean normalized = false;

    public DataPoint(double[] new_attributes, String new_class) {
        this.attributes = new_attributes;
        this.known_point_class = new_class;
    }

    public static DataPoint makeKnownDataPoint(String input_string, int num_attributes) {

        String[] input_array = input_string.split(",");
        double[] double_array = new double[num_attributes];
        for (int i = 0; i < num_attributes; i++) {
            double_array[i] = Double.parseDouble(input_array[i]);
        }
        String newclass = input_array[input_array.length - 1];

        DataPoint returnPoint = new DataPoint(double_array, newclass);
        return returnPoint;
    }

    public static DataPoint makeUnknownDataPoint(String input_string, int num_attributes) {

        String[] input_array = input_string.split(",");
        double[] double_array = new double[num_attributes];
        for (int i = 0; i < num_attributes; i++) {
            double_array[i] = Double.parseDouble(input_array[i]);
        }
        String newclass = "Unclassified";

        DataPoint returnPoint = new DataPoint(double_array, newclass);
        return returnPoint;

    }

    public static DataPoint makeKnownDataPoint(double[] new_attributes, String new_class) {
        DataPoint returnPoint = new DataPoint(new_attributes, new_class);
        return returnPoint;
    }


    /**
     * Calculates simple euclidean distance.
     *
     * @param other_point - point to check against
     * @return distance!
     */
    public double get_distance(DataPoint other_point) {
        double distance = 0;

        assert other_point.attributes.length == this.attributes.length;

        for (int i = 0; i < attributes.length; i++) {
            distance += Math.pow(this.attributes[i] - other_point.attributes[i], 2);
        }

        distance = Math.sqrt(distance);
        return distance;
    }

}
