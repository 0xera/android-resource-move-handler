package com.zeroxera.android.res

import com.zeroxera.android.res.expression.CompositeResourceAccessExpressionHandler
import com.zeroxera.android.res.expression.JavaResourceAccessExpressionHandler
import com.zeroxera.android.res.expression.KotlinResourceAccessExpressionHandler
import com.zeroxera.android.res.expression.XmlResourceAccessExpressionHandler
import com.zeroxera.android.res.utils.isInResFolder
import com.zeroxera.android.res.utils.searchAndroidResourceUsages
import com.zeroxera.android.res.utils.searchAndroidResourceUsagesRecursively
import com.intellij.lang.xml.XMLLanguage
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.light.LightElement
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler
import com.intellij.usageView.UsageInfo
import com.intellij.util.containers.MultiMap
import org.jetbrains.kotlin.idea.base.util.module

class AndroidResourceFileMoveHandler : MoveFileHandler() {

    private val handler = CompositeResourceAccessExpressionHandler(
        JavaResourceAccessExpressionHandler(),
        KotlinResourceAccessExpressionHandler(),
        XmlResourceAccessExpressionHandler()
    )

    private val singleFirDirsPrefixes = setOf(
        "drawable",
        "mipmap",
        "raw",
        "xml"
    )

    override fun canProcessElement(element: PsiFile): Boolean {
        return element.isInResFolder

    }

    override fun prepareMovedFile(
        element: PsiFile,
        directory: PsiDirectory,
        oldToNewMap: MutableMap<PsiElement, PsiElement>
    ) {
        val dstDirectory = DstDirectory(directory)
        oldToNewMap[dstDirectory] = dstDirectory
    }

    override fun findUsages(
        element: PsiFile,
        targetDirectory: PsiDirectory,
        searchInComments: Boolean,
        searchInNonJavaFiles: Boolean
    ): List<UsageInfo> {

        if (singleFirDirsPrefixes.any { element.parent?.name?.startsWith(it) == true }) {
            return element.searchAndroidResourceUsages()
        }

        return element.searchAndroidResourceUsagesRecursively()
            .filterNot { element.containingFile == it.element?.containingFile }

    }

    override fun retargetUsages(
        usageInfos: List<UsageInfo>,
        oldToNewMap: Map<PsiElement, PsiElement>
    ) {

        val dstDirectory = oldToNewMap.keys.firstOrNull() as? DstDirectory ?: return
        val destinationModule = dstDirectory.directory.module ?: return

        for (usageInfo in usageInfos) {
            usageInfo.element?.let { handler.handle(it, destinationModule) }
        }
    }

    override fun detectConflicts(
        conflicts: MultiMap<PsiElement, String>,
        elementsToMove: Array<out PsiElement>,
        usages: Array<out UsageInfo>,
        targetDirectory: PsiDirectory
    ) {
        val destinationModule = targetDirectory.module ?: return

        usages.forEach { usage ->
            val element = usage.element

            if (element != null && handler.hasConflict(element, destinationModule)) {
                conflicts.putValue(element, element.text)
            }
        }
    }

    override fun updateMovedFile(file: PsiFile) = Unit

    private class DstDirectory(
        val directory: PsiDirectory
    ) : LightElement(directory.manager, XMLLanguage.INSTANCE) {
        override fun toString() = ""
    }

}