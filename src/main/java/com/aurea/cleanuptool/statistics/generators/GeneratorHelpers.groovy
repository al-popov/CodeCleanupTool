package com.aurea.cleanuptool.statistics.generators

import org.apache.commons.lang.WordUtils

/**
 * Helpers for report generators
 */
class GeneratorHelpers {
    /**
     * Converts list to camel case style string
     * @param method - method name(words) stored in list
     * @return - camel case representation
     */
    static String toCamelCase(List<String> method) {
        boolean first = true
        String res = ""
        method.each {
            res += first ? it : WordUtils.capitalize(it)
            first = false
        }
        return res
    }

    /**
     * Convert string from ether came case or snake case representation to camel case
     * @param method - method name
     * @return = camel case representation
     */
    static String toCamelCase(String method) {
        List<String> methodCC = splitSnakeCase(method)
        if (methodCC.size() <= 1) {
            methodCC = splitCamelCase(method)
        }
        toCamelCase(methodCC)
    }

    /**
     * Splits camel case method name to list by words
     * @param method - method name
     * @return - list of words
     */
    static List<String> splitCamelCase(String method) {
        method.split(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])")).toList()
    }

    /**
     * Split snake case method name to list by words
     * @param method - method name
     * @return - list of words
     */
    static List<String> splitSnakeCase(String method) {
        method.split("_").toList()
    }
}
