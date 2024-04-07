package com.zeroxera.android.res.expression

import com.zeroxera.android.res.utils.canAccessToRClassFrom
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement

interface ResourceAccessExpressionHandler {

    fun canHandle(element: PsiElement): Boolean

    fun handle(element: PsiElement, destinationModule: Module)

    fun hasConflict(element: PsiElement, destinationModule: Module): Boolean {
        return if (canHandle(element)) !element.canAccessToRClassFrom(destinationModule) else false
    }

}