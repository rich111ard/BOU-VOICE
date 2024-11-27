package com.bou.bouvoice.ui.menu

import android.net.Uri

data class Nominee(
    val name: String,
    val department: String,
    val role: String,
    val imageUri: Uri? = null
)
