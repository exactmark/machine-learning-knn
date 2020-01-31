package com.company;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Harness for KNN classification.
 * 3 usage types, all depending on files formatted by csv,
 * x number of decimal attributes followed by string denoting class
 * <p>
 * args like: "cv-auto" "iris.data" "5" "120"
 * will run cross validation on given file, using 5 folds and k from 1-120.
 * <p>
 * args like: "iris_train.data" "4" "iris_test.data" "5"
 * Creates knn-model using iris_train.data and 4 attributes,
 * then run classification on iris_test.data with k-value of 5.
 * <p>
 * args like: ""
 * Guided usage, asking for training file and file to classify.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length > 0) {
            if (args[0].toLowerCase().equals("cv-auto")) {
                runCvAuto(args);
            } else {
                runKOnFiles(args);

            }

        } else {
            //No args, ask for guidance!
            runGuidedKNN();
        }

    }

    private static void runGuidedKNN() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Known data file (assumes csv, with numeric attributes before class): ");
        String datafile = scanner.nextLine();

        int numAttributes = guessNumAttributes(datafile);

        System.out.printf("Assuming number of attributes: %d\n", numAttributes);


        System.out.println("File to classify:");
        String classifyFile = scanner.nextLine();
        DataStore this_data = new DataStore(datafile, numAttributes);
        Knn classifier = new Knn(this_data);

        System.out.println("K value:");
        int k_value = scanner.nextInt();
        scanner.nextLine();

        classifier.classify_file(classifyFile, k_value);
    }

    private static void runKOnFiles(String[] args) {
        DataStore data = new DataStore(args[0], Integer.parseInt(args[1]));
        System.out.printf("Read %d entries\n", data.get_number_of_entries());

        Knn classifier = new Knn(data);

        String classifyFile = args[2];

        int k_value = Integer.parseInt(args[3]);

        classifier.classify_file(classifyFile, k_value);
    }

    private static void runCvAuto(String[] args) {
        int cv_fold = Integer.parseInt(args[2]);
        int k_value = Integer.parseInt(args[3]);
        System.out.println("Running " + cv_fold + "-fold cross validation on " + args[1]);
        DataStore data = new DataStore(args[1], guessNumAttributes(args[1]));
        System.out.println("Read " + data.get_number_of_entries() + " entries");

        System.out.printf("%5s%6s%10s%10s\n", "n", "k", "Accuracy", "Unclassed");

        for (int i = 1; i < k_value; i += 1) {
            KnnCrossValidator crossValidator = new KnnCrossValidator(data);
            KnnCrossValidator.AccuracyStore ResultsStore = crossValidator.DoCrossValidation(cv_fold, i);
            System.out.printf("%5d%6d%10.4f%10d\n", ResultsStore.getTotal(), i, ResultsStore.getAccuracy(), ResultsStore.undetermined);
        }


    }

    private static int guessNumAttributes(String input_file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(input_file));
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
//            e.printStackTrace();

        }
        String input_line = scanner.nextLine();
        int attrguess = input_line.split(",").length - 1;
        System.out.println("Sample line: " + input_line);
        System.out.println("Assuming " + attrguess + " attributes.");

        return input_line.split(",").length - 1;

    }

}
