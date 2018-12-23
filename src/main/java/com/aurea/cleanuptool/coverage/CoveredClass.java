package com.aurea.cleanuptool.coverage;


/**
 * Represents both class info and detailed method info
 */
public class CoveredClass {
    public String className;
    public String curClass;
    public String headline;
    public String access;
    public int loc = 0;
    public int uncovered = 0;
    public int methods = 0;
    public int clExtends = 0;
}
