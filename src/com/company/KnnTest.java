package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KnnTest {

    @Test
    void get_highest_map() {
        Map<String,Integer> test_map = new HashMap<>();

        test_map.put("Yes!",2);
        test_map.put("No",1);
        test_map.put("alsono",1);
        Assert.assertEquals("Yes!",new Knn().get_highest_map(test_map));

        test_map.clear();
        test_map.put("Single",1);
        Assert.assertEquals("Single",new Knn().get_highest_map(test_map));

        test_map.clear();
        Assert.assertNull(new Knn().get_highest_map(test_map));

        test_map.clear();
        test_map.put("Tie1",1);
        test_map.put("Tie2",1);
        Assert.assertNull(new Knn().get_highest_map(test_map));

    }
}