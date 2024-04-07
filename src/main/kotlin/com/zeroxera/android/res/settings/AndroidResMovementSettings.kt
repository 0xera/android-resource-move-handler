package com.zeroxera.android.res.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "com.zeroxera.android.res.settings.AndroidResMovementSettings",
    storages = [Storage("AndroidResMovementSettings.xml")]
)
internal class AndroidResMovementSettings :
    SimplePersistentStateComponent<AndroidResMovementSettings.State>(State()) {

    var shouldAddFIXMEToXml: Boolean
        set(value) {
            state.shouldAddFIXMEToXml = value
        }
        get() = state.shouldAddFIXMEToXml


    class State : BaseState() {

        var shouldAddFIXMEToXml by property(true)
    }

    companion object {
        fun getInstance(): AndroidResMovementSettings = ApplicationManager.getApplication().getService(
            AndroidResMovementSettings::class.java)
    }
}