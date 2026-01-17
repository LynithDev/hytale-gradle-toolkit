package dev.lynith.hytale.gradle.toolkit.ext

import dev.lynith.hytale.gradle.toolkit.models.HytalePluginAuthor
import dev.lynith.hytale.gradle.toolkit.utils.PascalCaseNamingStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class HytalePluginManifestExtension @Inject constructor(objects: ObjectFactory) {

    val mainClass: Property<String> = objects.property(String::class.java)
    val group: Property<String> = objects.property(String::class.java)
    val name: Property<String> = objects.property(String::class.java)
    val version: Property<String> = objects.property(String::class.java)

    val description: Property<String> = objects.property(String::class.java)
    val authors: ListProperty<HytalePluginAuthor> = objects.listProperty(HytalePluginAuthor::class.java)
    val website: Property<String> = objects.property(String::class.java)
    val serverVersion: Property<String> = objects.property(String::class.java)
    val dependencies: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)
    val optionalDependencies: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)
    val disabledByDefault: Property<Boolean> = objects.property(Boolean::class.java)

    val customEntries: MapProperty<String, JsonElement> = objects.mapProperty(String::class.java, JsonElement::class.java)

    fun author(author: String): HytalePluginAuthor = HytalePluginAuthor(author)
    fun author(configure: HytalePluginAuthor.() -> Unit): HytalePluginAuthor {
        val author = HytalePluginAuthor("")
        author.configure()

        authors.add(author)
        return author
    }

    fun entryOf(key: String, value: JsonElement) {
        customEntries.put(key, value)
    }

    fun entriesOf(map: Map<String, JsonElement>) {
        customEntries.putAll(map)
    }

}

@Serializable(with = HytalePluginManifestSerializer::class)
data class HytalePluginManifestConfig(
    @SerialName("main")
    var mainClass: String,
    var group: String,
    var name: String,
    var version: String,

    var description: String?,
    var authors: List<HytalePluginAuthor>,
    var website: String?,
    var serverVersion: String?,
    var dependencies: Map<String, String>,
    var optionalDependencies: Map<String, String>,
    var disabledByDefault: Boolean,

    val customEntries: Map<String, JsonElement>
) {

    @OptIn(ExperimentalSerializationApi::class)
    fun toJson(): String {
        val json = Json {
            prettyPrint = true
            explicitNulls = false
            namingStrategy = PascalCaseNamingStrategy()
        }

        return json.encodeToString(this)
    }
}

class HytalePluginManifestSerializer : KSerializer<HytalePluginManifestConfig> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("HytalePluginManifestConfig") {}

    override fun serialize(encoder: Encoder, value: HytalePluginManifestConfig) {
        require(encoder is JsonEncoder)

        val base = buildJsonObject {
            put("Main", JsonPrimitive(value.mainClass))
            put("Group", JsonPrimitive(value.group))
            put("Name", JsonPrimitive(value.name))
            put("Version", JsonPrimitive(value.version))
            value.description?.let { put("Description", JsonPrimitive(it)) }
            put("Authors", encoder.json.encodeToJsonElement(ListSerializer(HytalePluginAuthor.serializer()), value.authors))
            value.website?.let { put("Website", JsonPrimitive(it)) }
            value.serverVersion?.let { put("ServerVersion", JsonPrimitive(it)) }
            put("Dependencies", encoder.json.encodeToJsonElement(MapSerializer(String.serializer(), String.serializer()), value.dependencies))
            put("OptionalDependencies", encoder.json.encodeToJsonElement(MapSerializer(String.serializer(), String.serializer()), value.optionalDependencies))
            put("DisabledByDefault", JsonPrimitive(value.disabledByDefault))

            // customEntries needs to be flattened
            value.customEntries.forEach { (k, v) ->
                put(k, v)
            }
        }

        encoder.encodeJsonElement(base)
    }

    override fun deserialize(decoder: Decoder): HytalePluginManifestConfig {
        TODO("We don't ever deserialize the manifest so no need to implement this")
    }

}

fun HytalePluginExtension.resolveManifestConfig(project: Project): Provider<HytalePluginManifestConfig> {
    return project.provider {
        val override = requireNotNull(manifest.orNull) { "manifest must not be null" }

        val config = HytalePluginManifestConfig(
            mainClass = override.mainClass.getOrElse(""),
            group = override.group.getOrElse(project.group.toString()),
            name = override.name.getOrElse(project.name),
            version = override.version.getOrElse(project.version.toString()),

            description = override.description.getOrElse(project.description.orEmpty()).ifEmpty { null },
            authors = override.authors.getOrElse(listOf(
                HytalePluginAuthor(
                    name = "Unknown"
                )
            )),
            website = override.website.orNull,
            serverVersion = override.serverVersion.orNull,
            dependencies = override.dependencies.getOrElse(emptyMap()),
            optionalDependencies = override.optionalDependencies.getOrElse(emptyMap()),
            disabledByDefault = override.disabledByDefault.getOrElse(false),
            customEntries = override.customEntries.getOrElse(mapOf())
        )

        require(config.name.isNotBlank()) { "manifest name must not be blank" }
        require(config.group.isNotBlank()) { "manifest group must not be blank" }
        require(config.version.isNotBlank()) { "manifest version must not be blank" }
        require(config.mainClass.isNotBlank()) { "manifest main class must not be blank" }

        config
    }
}