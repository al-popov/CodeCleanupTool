package com.aurea.cleanuptool.source

import com.aurea.common.JavaClass
import com.github.javaparser.ast.CompilationUnit
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import java.nio.file.Path

@EqualsAndHashCode(excludes = 'cu')
@ToString(includePackage = false, includes = ['javaClass'])
class Unit {

    CompilationUnit cu
    JavaClass javaClass
    Path modulePath

    Unit(CompilationUnit cu, JavaClass javaClass, Path modulePath) {
        this.cu = cu
        this.javaClass = javaClass
        this.modulePath = modulePath
    }

    Unit(CompilationUnit cu, String className, String packageName, Path modulePath) {
        this(cu, new JavaClass(packageName, className), modulePath)
    }

    String getClassName() {
        javaClass.name
    }

    String getFullName() {
        javaClass.fullName
    }

    String getPackageName() {
        javaClass.package
    }
}
