package org.jayhsu.mangamore.data.enums

import androidx.annotation.StringRes
import org.jayhsu.mangamore.R

enum class ReadStatus(val id: Int, @StringRes val resid: Int){
    READING(1, R.string.read_status_type_reading),
    UNREAD(2, R.string.read_status_type_unread),
    READ(3,R.string.read_status_type_read);
}