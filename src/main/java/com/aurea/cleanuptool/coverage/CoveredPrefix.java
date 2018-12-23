package com.aurea.cleanuptool.coverage;

import java.util.TreeMap;
import java.util.Map;

/**
 * Information for methods prefixes coverage
 */
public class CoveredPrefix {
    public Map<String, CoveredPrefix> subLevel = new TreeMap<>();
    public int countInGroup = 0;
    public int uncoveredLoc = 0;
    public int totalLoc = 0;
    public float sqLocSum = 0;
    public int publicOccure = 0;
    public int privateOccure = 0;
    public int protectedOccure = 0;
}
