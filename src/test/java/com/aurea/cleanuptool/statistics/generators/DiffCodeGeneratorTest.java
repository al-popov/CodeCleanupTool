package com.aurea.cleanuptool.statistics.generators;

import static org.junit.Assert.assertEquals;

import difflib.DiffRow;
import org.junit.Test;
import java.util.List;

public class DiffCodeGeneratorTest {
    private static final String FIRST_TEST_STRING = "First test string";
    private static final String SECOND_TEST_STRING = "Second test string";
    private static final String FIRST_STRING_CHECK = "<span class=\"editOldInline\">First</span> test string";
    private static final String SECOND_STRING_CHECK = "<span class=\"editNewInline\">Second</span> test string";

    @Test
    public void getDiffRowsShouldGenerateEqualComparationForEqualString() {
        List<DiffRow> diffs =  DiffCodeGenerator.getDiffRows(FIRST_TEST_STRING, FIRST_TEST_STRING);

        assertEquals(diffs.size(),1);
        assertEquals(diffs.get(0).getTag(), DiffRow.Tag.EQUAL);
        assertEquals(diffs.get(0).getOldLine(), FIRST_TEST_STRING);
        assertEquals(diffs.get(0).getNewLine(), FIRST_TEST_STRING);
    }

    @Test
    public void getDiffRowsShouldGenerateDiffComparationForNotEqualString() {
        List<DiffRow> diffs =  DiffCodeGenerator.getDiffRows(FIRST_TEST_STRING, SECOND_TEST_STRING);

        assertEquals(diffs.size(),1);
        assertEquals(diffs.get(0).getTag(), DiffRow.Tag.CHANGE);
        assertEquals(diffs.get(0).getOldLine(), FIRST_STRING_CHECK);
        assertEquals(diffs.get(0).getNewLine(), SECOND_STRING_CHECK);
    }
}
