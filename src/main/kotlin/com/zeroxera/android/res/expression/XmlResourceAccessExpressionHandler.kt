package com.zeroxera.android.res.expression

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlAttributeValue
import com.zeroxera.android.res.settings.AndroidResMovementSettings

class XmlResourceAccessExpressionHandler : ResourceAccessExpressionHandler {

    private val settings = AndroidResMovementSettings.getInstance()

    override fun canHandle(element: PsiElement): Boolean = element is XmlAttributeValue

    override fun handle(element: PsiElement, destinationModule: Module) {
        if (element !is XmlAttributeValue || !canHandle(element)) return

        if (hasConflict(element, destinationModule) && settings.shouldAddFIXMEToXml) {

            val parent = element.parent
            val fixMeExpression = XmlElementFactory.getInstance(element.project).createDisplayText("FIXME_RES")
            parent.addAfter(fixMeExpression, parent.firstChild)
        }
    }

}
