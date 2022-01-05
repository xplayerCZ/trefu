package cz.davidkurzica.trefu.model

import kotlinx.serialization.Serializable

@Serializable
data class Stop(
    val id: Int,
    val name: String,
    val enabled: Boolean,
)