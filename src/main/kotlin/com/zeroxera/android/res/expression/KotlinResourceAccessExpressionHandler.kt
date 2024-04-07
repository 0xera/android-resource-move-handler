package com.zeroxera.android.res.expression

import com.zeroxera.android.res.utils.createRClassAccessExpression
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class KotlinResourceAccessExpressionHandler : ResourceAccessExpressionHandler {

    override fun canHandle(element: PsiElement) = element is KtNameReferenceExpression

    override fun handle(element: PsiElement, destinationModule: Module) {
        if (element !is KtNameReferenceExpression || !canHandle(element)) return

        val rClassExpression = createRClassAccessExpression(destinationModule) ?: return
        val ktPsiFactory = KtPsiFactory(element.project)

        val ktFile = element.containingFile as KtFile
        val import = ktFile.importList?.imports?.firstOrNull {
            it.importPath?.fqName?.asString() == rClassExpression
        }

        val alias = import?.aliasName

        if (alias != null && !element.parent.text.startsWith(alias)) {
            element.parent.replaceRClassAccess(ktPsiFactory.createExpression(alias))
        }

        if (import == null) {
            element.replaceRClassAccess(ktPsiFactory.createExpression(rClassExpression))
        }
    }


    private fun PsiElement.replaceRClassAccess(expression: PsiElement) {
        val rExpression = parent.children.first().children.first()
        rExpression.replace(expression)
    }
}