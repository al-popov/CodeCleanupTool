package com.aurea.cleanuptool.source;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about units found by Java Source Finder
 */
public class UnitSourceClass {
    public String packageName;
    public ClassOrInterfaceDeclaration declaration = new ClassOrInterfaceDeclaration();
    public List<MethodDeclaration> methods = new ArrayList<>();
    public String sourceCode;
}
