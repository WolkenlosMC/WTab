package de.theskyscout.wtab.utils

import de.theskyscout.wtab.config.Config
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

object UpdateChecker {

    fun checkForUpdate(plugin: JavaPlugin): Boolean{
        if (!Config.isUpdateNotify()) return false
        return getLatestVersion(plugin) != plugin.description.version
    }

    fun getLatestVersion(plugin: JavaPlugin): String {
        val url = URL("https://hangar.papermc.io/TheSkyScout/${plugin.name}/versions")
        val connection = url.openConnection()
        val inputStream = connection.getInputStream()

        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line!!.split(" ").forEach {
                    if(it.contains("SNAPSHOT") || it.contains("RELEASE"))
                        it.split("/").forEach { version ->
                            if(version.contains("SNAPSHOT") || version.contains("RELEASE"))
                                return version
                        }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}