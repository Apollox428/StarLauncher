using System;

namespace DreamLauncher.Records
{
    public enum VersionType
    {
        ALPHA,
        BETA,
        RELEASE,
        SNAPSHOT
    }

    public enum LoaderType
    {
        MOJANG,
        FABRIC,
        QUILT,
        NEOFORGE,
        FORGE,
        LEGACY_FABRIC
    }

    public class Version
    {
        public VersionType Type { get; }
        public string Identifier { get; }
        public DateTime? ReleaseDate { get; }
        public DateTime? LastModified { get; set; }
        public LoaderType LoaderType { get; set; }
        public string LoaderVersion { get; set; } = "latest";
        public bool IsInstalled { get; set; }
        public string Flags { get; set; } = "";
        public string FriendlyName { get; set; }

        // Constructor
        public Version(
            VersionType type,
            string identifier,
            DateTime? releaseDate,
            LoaderType loaderType,
            string loaderVersion = "latest",
            bool isInstalled = false,
            string flags = "")
        {
            Type = type;
            Identifier = identifier;
            ReleaseDate = releaseDate;
            LastModified = releaseDate;
            LoaderType = loaderType;
            LoaderVersion = loaderVersion;
            IsInstalled = isInstalled;
            Flags = flags;

            FriendlyName = ParseDisplayName(identifier, type, loaderType);
        }

        // Method equivalent to the Kotlin extension function getDisplayName
        public string GetDisplayName()
        {
            if (Identifier.Contains("fabric") || Identifier.Contains("forge") ||
                Identifier.Contains("neoforge") || Identifier.Contains("legacyfabric") || Identifier.Contains("quilt"))
            {
                return $"{LoaderTypeToString(LoaderType)}{VersionTypeToString(Type)}{Identifier.Split('-')[1]}";
            }
            else
            {
                return $"{VersionTypeToString(Type)}{Identifier}";
            }
        }

        private static string LoaderTypeToString(LoaderType loaderType)
        {
            switch (loaderType)
            {
                case LoaderType.FABRIC: return "Fabric ";
                case LoaderType.QUILT: return "Quilt ";
                case LoaderType.NEOFORGE: return "NeoForge ";
                case LoaderType.FORGE: return "Forge ";
                case LoaderType.LEGACY_FABRIC: return "Legacy Fabric ";
                default: return "";
            }
        }

        private static string VersionTypeToString(VersionType versionType)
        {
            switch (versionType)
            {
                case VersionType.SNAPSHOT: return "Snapshot ";
                case VersionType.BETA: return "Beta ";
                case VersionType.ALPHA: return "Alpha ";
                default: return "";
            }
        }

        // Static method equivalent to parseDisplayName
        public static string ParseDisplayName(string identifier, VersionType type, LoaderType loaderType)
        {
            if (identifier.Contains("fabric") || identifier.Contains("forge") ||
                identifier.Contains("neoforge") || identifier.Contains("legacyfabric") || identifier.Contains("quilt"))
            {
                return $"{LoaderTypeToString(loaderType)}{VersionTypeToString(type)}{identifier.Split('-')[1]}";
            }
            else
            {
                return $"{VersionTypeToString(type)}{identifier}";
            }
        }
    }

    public class Installation
    {
        public Version Version { get; }
        public string DisplayName { get; set; }
        public string Id { get; }

        public Installation(Version version)
        {
            Version = version;
            DisplayName = version.GetDisplayName();
            Id = version.Identifier;
        }
    }
}