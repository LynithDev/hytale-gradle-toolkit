package dev.lynith.hytale.gradle.toolkit.models

import kotlinx.serialization.Serializable

@Serializable
data class HytalePluginAuthor(
    var name: String,
    var email: String? = null,
    var url: String? = null,
)