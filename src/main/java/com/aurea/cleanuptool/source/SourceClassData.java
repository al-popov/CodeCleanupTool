package com.aurea.cleanuptool.source;

import com.aurea.coverage.unit.MethodCoverage;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceClassData {
    public String packageName;
    public ClassOrInterfaceDeclaration declaration;
    public List<MethodDeclaration> methods = new ArrayList<>();
    public Set<String> methodNames = new HashSet<>();
    public Set<String> methodSignature = new HashSet<>();
    public List<SourceClassData> references;
    public List<Integer> duplicationCount;
    public List<Float> similarPercent;
    public MethodCoverage methodCoverage;
    public String sourceCode;
}
