package org.sdvina.mangamore.data.enums

import org.sdvina.mangamore.R

enum class FolderType(val id: Int, val resid: Int) {
    LOCAL(1, R.string.folder_type_local),
    CLOUD(2, R.string.folder_type_cloud);
}