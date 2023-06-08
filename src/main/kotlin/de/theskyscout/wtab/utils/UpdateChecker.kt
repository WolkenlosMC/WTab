package de.theskyscout.wtab.utils

import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.URL

object UpdateChecker {

    fun checkForUpdate(plugin: JavaPlugin): Boolean{
        return getLatestVersion(plugin) != plugin.description.version
    }

    fun getLatestVersion(plugin: JavaPlugin): String {
        try {
            val url = URL("https://hangar.papermc.io/TheSkyScout/${plugin.name}/versions")
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "WTab")
            connection.connect()
            val inputStream = connection.getInputStream()
            val reader = inputStream.bufferedReader()
            var version = ""
            for (it in reader.readLines()) {
                if(it.contains("<h2 class=\"lg:basis-full lt-lg:mr-1 text-1.15rem leading-relaxed\">") && it.contains("</h2>")) {
                    if (it.contains("SNAPSHOT") || it.contains("RELEASE")) {
                        val string = it.replace("<h2 class=\"lg:basis-full lt-lg:mr-1 text-1.15rem leading-relaxed\">", "")
                            .replace("</h2>", "")
                        string.split(" ",).forEach {
                            for (it in it.split(",")) {
                                if (it.contains("SNAPSHOT") || it.contains("RELEASE")) {
                                    version = it.replace("href=\"https://hangarcdn.papermc.io/plugins/TheSkyScout/WTab/versions/", "").split("/")[0]
                                    return version
                                }
                            }
                        }
                        break
                    }
                }
            }
            return version
        }catch (e: IOException) {
            plugin.logger.warning("Cannot look for updates! Error: ${e.message}")
        }
        return ""
    }
}