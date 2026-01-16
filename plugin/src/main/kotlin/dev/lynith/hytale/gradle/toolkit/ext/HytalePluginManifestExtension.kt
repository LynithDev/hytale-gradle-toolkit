package dev.lynith.hytale.gradle.toolkit.ext

import dev.lynith.hytale.gradle.toolkit.models.HytalePluginAuthor
import dev.lynith.hytale.gradle.toolkit.utils.PascalCaseNamingStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

    fun author(author: String): HytalePluginAuthor = HytalePluginAuthor(author)
    fun author(configure: HytalePluginAuthor.() -> Unit): HytalePluginAuthor {
        val author = HytalePluginAuthor("")
        author.configure()

        authors.add(author)
        return author
    }
}

@Serializable
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
        )

        require(config.name.isNotBlank()) { "manifest name must not be blank" }
        require(config.group.isNotBlank()) { "manifest group must not be blank" }
        require(config.version.isNotBlank()) { "manifest version must not be blank" }
        require(config.mainClass.isNotBlank()) { "manifest main class must not be blank" }

        config
    }
}