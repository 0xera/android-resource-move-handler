package com.zeroxera.android.res.expression

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement

class CompositeResourceAccessExpressionHandler private constructor(
    private val handlers: List<ResourceAccessExpressionHandler>
) : ResourceAccessExpressionHandler {

    constructor(vararg handler: ResourceAccessExpressionHandler) : this(handler.toList())

    override fun canHandle(element: PsiElement): Boolean = handlers.any { it.canHandle(element) }

    override fun handle(element: PsiElement, destinationModule: Module) {
        handlers.forEach { it.handle(element, destinationModule) }
    }

    override fun hasConflict(element: PsiElement, destinationModule: Module): Boolean =
        handlers.any { it.hasConflict(element, destinationModule) }
}


