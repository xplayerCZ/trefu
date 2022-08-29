package cz.davidkurzica.trefu.domain.util

import androidx.annotation.StringRes

data class ErrorMessage(val id: Long, @StringRes val messageId: Int)