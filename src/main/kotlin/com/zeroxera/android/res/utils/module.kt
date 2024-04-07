package com.zeroxera.android.res.utils

import com.android.tools.idea.gradle.project.model.GradleAndroidModel
import com.android.tools.idea.model.AndroidModel
import com.intellij.openapi.module.Module

fun createRClassAccessExpression(module: Module): String? {
    val gradleAndroidModel = AndroidModel.get(module) as? GradleAndroidModel
    return gradleAndroidModel?.androidProject?.namespace?.let { "${it}.R" }
}