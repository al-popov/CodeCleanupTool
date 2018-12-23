package com.aurea.cleanuptool.ast

import com.github.javaparser.ast.Node
import org.springframework.context.ApplicationEvent


class UnsolvedDeclarationEvent extends ApplicationEvent {

    Node node
    UnsolvedDeclarationType type

    enum UnsolvedDeclarationType {
        DECLARATION,
        TYPE_RESOLUTION,
        TYPE_CALCULATION
    }

    UnsolvedDeclarationEvent(Object source, Node node, UnsolvedDeclarationType type) {
        super(source)
        this.node = node
        this.type = type
    }
}
