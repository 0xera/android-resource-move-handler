package com.zeroxera.android.res.settings

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel

internal class AndroidResMovementConfigurable : BoundSearchableConfigurable("Android Resource Move Handler", "preferences.0xeraandroidres") {

    private val settings = AndroidResMovementSettings.getInstance()

    override fun createPanel(): DialogPanel =
        panel {
            row {
                checkBox("Add 'FIXME_RES' text to xml when resolving resource is failed after moving the file")
                    .bindSelected(settings::shouldAddFIXMEToXml)
            }
        }
}