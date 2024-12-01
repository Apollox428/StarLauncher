package ui.nativelook.windows.jna.structs

import com.sun.jna.Structure

internal open class BaseStructure : Structure(), Structure.ByReference {
    open fun dispose() = clear()
}