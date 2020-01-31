package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnnCrossValidator {

    private DataStore base_datastore;
    private List<Integer> integerList = new ArrayList<>();
    private List<List<Integer>> cv_populations = new ArrayList<>();
    private AccuracyStore cvAccuracyStore = new AccuracyStore();

    /**
     * A simple class to hold results from cross validation.
     */
    public class AccuracyStore {
        public int correct = 0;
        public int incorrect = 0;
        public int undetermined = 0;

        public float getAccuracy() {
            return ((float) correct) / (getTotal());
        }

        public int getTotal() {
            return correct + incorrect + undetermined;
        }

    }

//    Method for cross validation
//    a) take the datastore,
//    b) create a list of datastore.entrycount integers,
//    c) Shuffle those integers
//    d) create N groups from this list of integers.
//    e) Create training and test datastores.
//    f) create Knn model using training
//    g) classify points in test datastore using model
//    h) Store results to AccuracyStore

    public KnnCrossValidator(DataStore input) {
        this.base_datastore = input;
    }

    /**
     * Does cross validation using parameters specified. Returns accuracy store
     * @param cv_fold_count Number of folds for cv
     * @param k K value to classify
     * @return Result class with relevant values.
     */
    public AccuracyStore DoCrossValidation(int cv_fold_count, int k) {

        assert cv_fold_count > 1;

        //clear the result class
        this.cvAccuracyStore = new AccuracyStore();

        CreateSubGroups(cv_fold_count);

        DoCrossValidationLogic(cv_fold_count, k);
        return this.cvAccuracyStore;
    }

    /**
     * Creates a unique shuffled list of integers for each cv_fold. Each int corresponds
     * to an index position in original datastore. I'm sure there are better ways to do this.
     * @param cv_fold_count number of crossvalidations
     */
    private void CreateSubGroups(int cv_fold_count) {
        // Create list of integers for index positions in datastore
        for (int i = 0; i < base_datastore.get_number_of_entries(); i++) {
            integerList.add(i);
        }

        //Shuffle the index list
        Collections.shuffle(integerList);

        //make sure we have a new set
        this.cv_populations.clear();

        //Create a population list for each cv_fold_count.
        for (int i = 0; i < cv_fold_count; i++) {
            List<Integer> SingleList = new ArrayList<>();
            this.cv_populations.add(SingleList);
        }

        //Stick shuffled integer indexes in populations, one at a time.
        for (int i = 0; i < this.integerList.size(); i++) {
            this.cv_populations.get(i % cv_fold_count).add(this.integerList.get(i));
        }

    }

    /**
     * Does the actual work of the cross validation.
     * @param cv_fold_count Numver of cv folds
     * @param k K-value for this knn model.
     */
    private void DoCrossValidationLogic(int cv_fold_count, int k) {
        assert this.cv_populations.size() > 0;
        //Starting cross validation
        for (int cv_test_index = 0; cv_test_index < cv_fold_count; cv_test_index++) {
            //make a training list of all indexes not in test set
            List<Integer> training_list = new ArrayList<>();
            for (int j = 0; j < cv_fold_count; j++) {
                if (j != cv_test_index) {
                    training_list.addAll(cv_populations.get(j));
                }
            }

            // Create training and test sets.
            DataStore TrainingData = base_datastore.GetSubCopy(training_list);
            DataStore TestData = base_datastore.GetSubCopy(this.cv_populations.get(cv_test_index));
            Knn single_validator = new Knn(TrainingData);
            single_validator.classify_datastore(TestData, k);
            for (DataPoint PredictedPoint :
                    TestData.normalized_data_store) {
                if (PredictedPoint.predicted_point_class == null) {
                    this.cvAccuracyStore.undetermined += 1;
                } else if (PredictedPoint.known_point_class.equals(PredictedPoint.predicted_point_class)) {
                    this.cvAccuracyStore.correct += 1;
                } else {
                    this.cvAccuracyStore.incorrect += 1;
                }
            }
        }

    }

}
