package com.aurea.cleanuptool.coverage;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

/**
 * Information for class suffixes coverage
 */
public class CoveredSuffix {
    public Map<String, CoveredSuffix> subLevel = new TreeMap<>();
    public int countInGroup = 0;
    public int uncoveredLoc = 0;
    public int totalMethods = 0;
    public float sqMethodSum = 0;
    public List<CoveredClass> implementation;
}
