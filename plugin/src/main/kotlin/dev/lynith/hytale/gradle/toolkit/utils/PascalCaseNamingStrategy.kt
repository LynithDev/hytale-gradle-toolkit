package dev.lynith.hytale.gradle.toolkit.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
class PascalCaseNamingStrategy : JsonNamingStrategy {
    override fun serialNameForJson(
        descriptor: SerialDescriptor,
        elementIndex: Int,
        serialName: String
    ): String {
        return serialName.replaceFirstChar { it.titlecase() }
    }
}