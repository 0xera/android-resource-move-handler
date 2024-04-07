package com.zeroxera.android.res.utils

import com.android.tools.idea.res.psi.ResourceReferencePsiElement
import com.intellij.find.FindManager
import com.intellij.find.impl.FindManagerImpl
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlTag
import com.intellij.usageView.UsageInfo
import org.jetbrains.android.AndroidResolveScopeEnlarger
import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.kotlin.idea.base.psi.childrenDfsSequence
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.psiUtil.parents

val PsiFile.isInResFolder: Boolean
    get() {
        val resDirectory = parent?.parent ?: return false
        val module = module ?: return false
        val project = resDirectory.project

        val folder = ProjectRootsUtil.getModuleSourceRoot(resDirectory.virtualFile, project)
        return folder != null && folder.contentEntry.rootModel.module == module && folder.rootType == JavaResourceRootType.RESOURCE
    }


fun PsiFile.searchAndroidResourceUsages(): List<UsageInfo> {

    val refUsages = mutableListOf<UsageInfo>()

    val referencePsiElement = ResourceReferencePsiElement.create(this)
    val findUsagesManager = (FindManager.getInstance(project) as FindManagerImpl).findUsagesManager
    val refFindUsagesHandler = findUsagesManager.getNewFindUsagesHandler(this, false)

    if (referencePsiElement == null || refFindUsagesHandler == null) return refUsages

    refFindUsagesHandler.processElementUsages(referencePsiElement, {
        refUsages += it
        true
    }, refFindUsagesHandler.getFindUsagesOptions(null))

    return refUsages
}

fun PsiFile.searchAndroidResourceUsagesRecursively(): List<UsageInfo> {

    val refUsages = mutableListOf<UsageInfo>()

    val referencePsiElements = childrenDfsSequence()
        .map(ResourceReferencePsiElement.Companion::create)
        .filterNotNull()
        .ignoreStyleableAttrs()
        .toMutableList()

    val findUsagesManager = (FindManager.getInstance(project) as FindManagerImpl).findUsagesManager
    val refFindUsagesHandler = findUsagesManager.getNewFindUsagesHandler(this, false)

    if (referencePsiElements.isEmpty() || refFindUsagesHandler == null) return refUsages

    referencePsiElements.forEach { resElement ->
        refFindUsagesHandler.processElementUsages(resElement, {
            refUsages += it
            true
        }, refFindUsagesHandler.getFindUsagesOptions(null))
    }

    return refUsages
}

private fun Sequence<ResourceReferencePsiElement>.ignoreStyleableAttrs(): Sequence<ResourceReferencePsiElement> =
    map(ResourceReferencePsiElement.Companion::create)
     .filterNotNull()
     .map { element ->
         val parentTag = element.parents.firstOrNull { it is XmlTag && it.localName == "declare-styleable" }
         element.takeIf { parentTag == null }
     }
     .filterNotNull()

fun PsiElement.canAccessToRClassFrom(module: Module): Boolean {
    val refFile = containingFile.virtualFile
    val refModule = ModuleUtilCore.findModuleForFile(refFile, project) ?: return false

    val rClass = createRClassAccessExpression(module) ?: return false
    val psiFacade = JavaPsiFacade.getInstance(project)
    val scopeForModule =
        AndroidResolveScopeEnlarger.getAdditionalResolveScopeForModule(refModule, false) ?: return false

    return psiFacade.findClass(rClass, GlobalSearchScope.EMPTY_SCOPE.union(scopeForModule)) != null
}