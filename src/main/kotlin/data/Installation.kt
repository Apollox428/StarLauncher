package data

data class Installation(
    val version: Version,
    var displayName: String = parseDisplayName(version),
    val id: String = version.identifier
)

fun parseDisplayName(version: Version): String {
    return if (
        version.identifier.contains("fabric") ||
        version.identifier.contains("forge") ||
        version.identifier.contains("neoforge") ||
        version.identifier.contains("legacyfabric") ||
        version.identifier.contains("quilt")
    ) {
        "".plus(
            when (version.loaderType) {
                LoaderType.MOJANG -> ""
                LoaderType.FABRIC -> "Fabric "
                LoaderType.QUILT -> "Quilt "
                LoaderType.NEOFORGE -> "NeoForge "
                LoaderType.FORGE -> "Forge "
                LoaderType.LEGACY_FABRIC -> "Legacy Fabric "
            }
        ).plus(
            when (version.type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(version.identifier.split("-")[1])
    } else {
        "".plus(
            when (version.type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(version.identifier)
    }
}
