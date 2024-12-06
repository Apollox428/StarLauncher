package data

import java.time.LocalDate
import java.time.LocalDateTime

import java.time.format.DateTimeFormatter
import java.util.*


data class Version(
    val type: VersionType,
    val identifier: String,
    val releaseDate: LocalDateTime?,
    var lastModified: LocalDateTime? = releaseDate,
    var loaderType: LoaderType,
    var loaderVersion: String = "latest",
    var isInstalled: Boolean,
    val flags: String = "",
    val friendlyName: String = parseDisplayName(identifier, type, loaderType)
)

enum class VersionType { ALPHA, BETA, RELEASE, SNAPSHOT }
enum class LoaderType { MOJANG, FABRIC, QUILT, NEOFORGE, FORGE, LEGACY_FABRIC }

fun parseStringTimestamp(string: String): LocalDateTime? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy", Locale.ENGLISH).withResolverStyle(java.time.format.ResolverStyle.LENIENT)
        LocalDateTime.parse(string.replace(Regex("\\s+"), " "), formatter)
    } catch (e: Exception) {
        null
    }
}