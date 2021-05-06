package com.dscvit.songified.util

import android.content.DialogInterface

interface DialogDismissListener {
    fun handleDialogClose(dialog: DialogInterface, isSignedIn: Boolean)
}