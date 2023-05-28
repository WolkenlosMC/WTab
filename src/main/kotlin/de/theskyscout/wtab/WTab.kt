package de.theskyscout.wtab

import de.theskyscout.wtab.commands.WTabCommand
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.listeners.MessageListeners
import de.theskyscout.wtab.manager.TablistManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class WTab : JavaPlugin() {

    companion object {
        lateinit var instance: WTab
    }

    override fun onEnable() {
        instance = this
        Config.load()
        Config.loadLuckPerms()
        MongoDB.connect()
        registerCommands()
        registerListeners()
        TablistManager.updateTablist()
    }

    private fun registerCommands() {
        getCommand("wtab")?.setExecutor(WTabCommand())
        getCommand("wtab")?.tabCompleter = WTabCommand()
    }

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(MessageListeners(), this)
    }
}