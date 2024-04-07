package com.zeroxera.android.res.expression

import com.zeroxera.android.res.utils.createRClassAccessExpression
import com.intellij.openapi.module.Module
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiReferenceExpression

class JavaResourceAccessExpressionHandler : ResourceAccessExpressionHandler {

    override fun canHandle(element: PsiElement) = element is PsiReferenceExpression

    override fun handle(element: PsiElement, destinationModule: Module) {
        if (element !is PsiReferenceExpression || !canHandle(element)) return

        val rClassExpression = createRClassAccessExpression(destinationModule) ?: return

        val javaFile = element.containingFile as PsiJavaFile
        val import = javaFile.importList?.importStatements?.firstOrNull {
            it.qualifiedName == rClassExpression
        }

        if (import == null) {
            val psiElementFactory = JavaPsiFacade.getElementFactory(element.project)
            element.replaceRClassAccess(psiElementFactory.createExpressionFromText(rClassExpression, null))
        }
    }

    private fun PsiElement.replaceRClassAccess(expression: PsiElement) {
        val rExpression = children.first().children.first()
        rExpression.replace(expression)
    }
}