package com.aurea.cleanuptool.coverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Covered method agregated information (for method group with the same name)
 */
public class CoveredMethod {
    public List<CoveredClass> rootClass = new ArrayList<>();
    public Set<String> paramSet = new HashSet<>();
    public Set<String> returnSet = new HashSet<>();
    public int instructionUncovered = 0;
    public int instructionCovered = 0;
    public int count = 0;
    public float sqLocSum = 0;
    public int totalLoc = 0;
    public int publicOccure = 0;
    public int privateOccure = 0;
    public int protectedOccure = 0;
}
