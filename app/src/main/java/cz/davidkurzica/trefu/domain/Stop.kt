package cz.davidkurzica.trefu.domain

import kotlinx.serialization.Serializable

@Serializable
data class StopOption(
    val id: Int,
    val name: String,
    val enabled: Boolean,
)