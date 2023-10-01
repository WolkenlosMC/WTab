package de.theskyscout.wtab.config

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.manager.TablistManager
import de.theskyscout.wtab.utils.ConfigUtil
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object Config {

    fun load() {
        ConfigUtil("config.yml")
    }

    fun get(key: String): Any? {
        return ConfigUtil("config.yml").config.get(key)
    }

    fun set(key: String, value: Any) {
        ConfigUtil("config.yml").config.set(key, value)
    }

    fun prefix(): String {
        return ConfigUtil("config.yml").config.getString("prefix")!!
    }

    fun saveMethodIsFile(): Boolean {
        return ConfigUtil("config.yml").config.getString("save-method") == "FILE"
    }

    fun saveMethodIsMongoDB(): Boolean {
        return ConfigUtil("config.yml").config.getString("save-method") == "MONGODB"
    }

    fun isLuckperms(): Boolean {
        return ConfigUtil("config.yml").config.getString("tablist-method") == "LUCKPERMS"
    }

    fun isPerms(): Boolean {
        return ConfigUtil("config.yml").config.getString("tablist-method") == "PERMISSION"
    }

    fun mongoURI(): String {
        return ConfigUtil("config.yml").config.getString("mongo_uri")!!
    }

    fun save() {
        ConfigUtil("config.yml").save()
    }

    fun loadLuckPerms() {
        if(isLuckperms()) {
            if(Bukkit.getServer().pluginManager.getPlugin("LuckPerms") == null) {
                WTab.instance.logger.severe("LuckPerms is not installed!")
                WTab.instance.logger.severe("Please install LuckPerms or change the tablist-method in the config.yml to PERMISSION")
                WTab.enabled = false
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.getPluginManager().disablePlugin(WTab.instance)
                    }
                }.runTaskLater(WTab.instance, 20)
            } else {
                LuckPermsProvider.get()
            }
        }
    }

    fun checkLuckPerms() : Boolean {
        if(!WTab.enabled) return false
        if(isLuckperms()) {
            if(Bukkit.getServer().pluginManager.getPlugin("LuckPerms") == null) {
                WTab.instance.logger.severe("LuckPerms is not installed!")
                WTab.instance.logger.severe("Please install LuckPerms or change the tablist-method in the config.yml to PERMISSION")
                TablistManager.tablistTask!!.cancel()
                WTab.enabled = false
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.getPluginManager().disablePlugin(WTab.instance)
                    }
                }.runTaskLater(WTab.instance, 20)
                return false
            }
            return true
        }
        return false
    }

}