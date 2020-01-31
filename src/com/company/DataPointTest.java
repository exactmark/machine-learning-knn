package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataPointTest {

    private DataPoint makePoint(String thisclass, double x, double y) {
        double[] attr = new double[2];
        attr[0] = x;
        attr[1] = y;

        return new DataPoint(attr, thisclass);
    }

    @Test
    void get_distance() {

        double[][] test_cases = new double[][]{{0, 0, 1, 1, 1.412}, {0, 0, 3, 4, 5}};
        for (double[] testarray :
                test_cases) {
            DataPoint left_point = makePoint("Left", testarray[0], testarray[1]);
            DataPoint right_point = makePoint("Right", testarray[2], testarray[3]);
            Assert.assertEquals(testarray[4], left_point.get_distance(right_point), .01);
        }

    }
}