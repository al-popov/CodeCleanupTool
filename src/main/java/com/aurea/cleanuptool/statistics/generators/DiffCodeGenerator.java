package com.aurea.cleanuptool.statistics.generators;

import difflib.DiffRow;
import difflib.DiffRowGenerator;
import java.util.Arrays;
import java.util.List;

public class DiffCodeGenerator {
    /**
     * Create diff for two code sample base on difflib from com.googlecode.java-diff-utils
     *
     * @param originalCode - original code
     * @param revisedCode  - revised code
     * @return - list of difference in two code sample
     */
    public static List<DiffRow> getDiffRows(String originalCode, String revisedCode) {
        final List<String> originalFileLines = Arrays.asList(originalCode.split("\r?\n"));
        final List<String> revisedFileLines = Arrays.asList(revisedCode.split("\r?\n"));

        DiffRowGenerator generator = new DiffRowGenerator.Builder()
                .showInlineDiffs(true)
                .ignoreWhiteSpaces(true)
                .columnWidth(120)
                .build();

        return generator.generateDiffRows(originalFileLines, revisedFileLines);
    }
}