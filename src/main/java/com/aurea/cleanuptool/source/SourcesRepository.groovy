package com.aurea.cleanuptool.source

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.util.logging.Log4j2
import org.springframework.stereotype.Component

/**
 * Repository for java parser source files
 */
@Component
@Log4j2
class SourcesRepository {
    private final Map<String, UnitSourcePackage> unitSourcePackages = new HashMap<>()

    /**
     * Parse each unti in source repository
     * @param unit - parsing unit
     * @return - aggregated information
     */
    UnitSourceClass parseUnit(Unit unit) {
        if (!unitSourcePackages.containsKey(unit.packageName)) {
            unitSourcePackages[unit.packageName] = new UnitSourcePackage()
        }

        UnitSourceClass newClass = new UnitSourceClass()

        newClass.methods = unit.cu.findAll(MethodDeclaration.class)
        newClass.declaration = unit.cu.findAll(ClassOrInterfaceDeclaration.class)[0]
        newClass.packageName = unit.packageName
        newClass.sourceCode = unit.cu.toString()

        unitSourcePackages[unit.packageName].classes[unit.className] = newClass
        newClass
    }
}
