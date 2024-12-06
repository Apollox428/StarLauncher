package data

data class Installation(
    val version: Version,
    var displayName: String = version.getDisplayName(),
    val id: String = version.identifier
)

fun Version.getDisplayName(): String {
    return if (
        this.identifier.contains("fabric") ||
        this.identifier.contains("forge") ||
        this.identifier.contains("neoforge") ||
        this.identifier.contains("legacyfabric") ||
        this.identifier.contains("quilt")
    ) {
        "".plus(
            when (this.loaderType) {
                LoaderType.MOJANG -> ""
                LoaderType.FABRIC -> "Fabric "
                LoaderType.QUILT -> "Quilt "
                LoaderType.NEOFORGE -> "NeoForge "
                LoaderType.FORGE -> "Forge "
                LoaderType.LEGACY_FABRIC -> "Legacy Fabric "
            }
        ).plus(
            when (this.type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(this.identifier.split("-")[1])
    } else {
        "".plus(
            when (this.type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(this.identifier)
    }
}

fun parseDisplayName(identifier: String, type: VersionType, loaderType: LoaderType): String {
    return if (
        identifier.contains("fabric") ||
        identifier.contains("forge") ||
        identifier.contains("neoforge") ||
        identifier.contains("legacyfabric") ||
        identifier.contains("quilt")
    ) {
        "".plus(
            when (loaderType) {
                LoaderType.MOJANG -> ""
                LoaderType.FABRIC -> "Fabric "
                LoaderType.QUILT -> "Quilt "
                LoaderType.NEOFORGE -> "NeoForge "
                LoaderType.FORGE -> "Forge "
                LoaderType.LEGACY_FABRIC -> "Legacy Fabric "
            }
        ).plus(
            when (type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(identifier.split("-")[1])
    } else {
        "".plus(
            when (type) {
                VersionType.RELEASE -> ""
                VersionType.SNAPSHOT -> "Snapshot "
                VersionType.BETA -> "Beta "
                VersionType.ALPHA -> "Alpha "

            }
        ).plus(identifier)
    }
}
