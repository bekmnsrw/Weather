package com.example.android1.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG
) = Snackbar
    .make(this, message, duration)
    .show()
