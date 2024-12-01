package utils

import androidx.compose.runtime.MutableState
import com.pty4j.PtyProcessBuilder
import com.pty4j.WinSize
import data.*
import enums.Screen
import kotlinx.coroutines.*
import viewmodel.AppViewModel
import viewmodel.MainViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class Launcher(private val mainViewModel: MainViewModel, private val appViewModel: AppViewModel) {

    val versions = mutableListOf<Version>()
    val installations = mutableListOf<Installation>()

    suspend fun loadVersions() {
        //Get available mojang versions
        startProcess(
            command = "python -m portablemc --output machine search",
            onNewLine = {
                if (!it.isNullOrBlank() && it.contains("row:") && !it.contains("row:Type,Identifier,Release date,Flags")) {
                    val data = it.replace("row:", "").split(",")
                    val type = when (data[0]) {
                        "old_alpha" -> VersionType.ALPHA
                        "old_beta" -> VersionType.BETA
                        "release" -> VersionType.RELEASE
                        "snapshot" -> VersionType.SNAPSHOT
                        else -> VersionType.SNAPSHOT
                    }
                    versions.add(
                        Version(
                            type = type,
                            identifier = data[1],
                            releaseDate = parseStringTimestamp(data[2]),
                            loaderType = LoaderType.MOJANG,
                            isInstalled = it.contains("local")
                        )
                    )
                }
            }
        )
        startProcess(
            command = "python -m portablemc --output machine search --kind local",
            onNewLine = {
                if (!it.isNullOrBlank() && it.contains("row:") && !it.contains("row:Identifier,Last modified")) {
                    val data = it.replace("row:", "").split(",")
                    if (
                        data[0].contains("fabric") ||
                        data[0].contains("forge") ||
                        data[0].contains("neoforge") ||
                        data[0].contains("legacyfabric") ||
                        data[0].contains("quilt")
                    ) {
                        val newData = data[0].split("-")
                        val loaderType = when (newData[0]) {
                            "fabric" -> LoaderType.FABRIC
                            "forge" -> LoaderType.FORGE
                            "neoforge" -> LoaderType.NEOFORGE
                            "legacyfabric" -> LoaderType.LEGACY_FABRIC
                            "quilt" -> LoaderType.QUILT
                            else -> LoaderType.MOJANG
                        }
                        val loaderVersion = newData[2]
                        val originalVersion = versions.first { version -> version.identifier == newData[1] }
                        versions.add(
                            Version(
                                type = originalVersion.type,
                                identifier = data[0],
                                releaseDate = originalVersion.releaseDate,
                                lastModified = parseStringTimestamp(data[1]),
                                loaderVersion = loaderVersion,
                                loaderType = loaderType,
                                isInstalled = true
                            )
                        )
                    }
                }
            }
        )

        //MUST REPLACE FUNCTIONALITY
        val installedVersions = versions.filter { version -> version.isInstalled }
        for (version in installedVersions) {
            installations.add(Installation(version))
        }

        println(versions)
        println("Version list loaded!")
    }
    private suspend fun startProcess(
        command: String,
        output: MutableState<List<String>>? = null,
        shouldOpenConsole: Boolean = false,
        shouldShowCommand: Boolean = false,
        onNewLine: (String?) -> Unit = {},
    ): Int {
        // Initialize a local mutable list to collect process output
        val commands = arrayOf("cmd.exe", "/c", command)
        val processBuilder = PtyProcessBuilder(commands)
        processBuilder.setRedirectErrorStream(true)  // Combine stdout and stderr

        return withContext(Dispatchers.IO) { // Run the process entirely in IO
            val process = processBuilder.start()
            process.winSize = WinSize(10240, 10240)
            if (shouldOpenConsole) mainViewModel.diagWindow.value = true

            try {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    val outputList = mutableListOf<String>()
                    if (shouldShowCommand) outputList.add("§bCommand: $command\n")
                    while (reader.readLine().also { line = it } != null) {
                        onNewLine(line)
                        println(line)  // Print output in real-time
                        outputList.add(line.toString())
                    }

                    if (output != null) {
                        // Safely update the clientOutput state with the collected output
                        withContext(Dispatchers.Default) {
                            appViewModel.clientOutput.value = outputList
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.toString())
            }

            val exitCode = process.waitFor()
            if (output != null) {
                withContext(Dispatchers.Default) {
                    // Add exit code to the output list
                    appViewModel.clientOutput.value += "${if (exitCode != 0) "§c" else "§a"}Process exited with code $exitCode"
                    println("Process exited with code $exitCode")
                }
            }
            exitCode
        }
    }

}