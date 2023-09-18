package de.theskyscout.wtab

import de.theskyscout.wtab.commands.WTabCommand
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.listeners.MessageListeners
import de.theskyscout.wtab.manager.DataCaching
import de.theskyscout.wtab.manager.TablistManager
import de.theskyscout.wtab.utils.UpdateChecker
import de.theskyscout.wtab.utils.proxies.ProxyMessageChannel
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class WTab : JavaPlugin() {

    companion object {

        lateinit var instance: WTab
        var enabled :Boolean = false
    }

    override fun onEnable() {
        enabled = true
        instance = this
        Config.load()
        Config.loadLuckPerms()
        MongoDB.connect()
        DataCaching.refreshCache(this)
        registerCommands()
        registerListeners()
        registerOther()
    }

    override fun onDisable() {
        ProxyMessageChannel.unregisterChannel()
        enabled = false
    }

    private fun registerCommands() {
        getCommand("wtab")?.setExecutor(WTabCommand())
        getCommand("wtab")?.tabCompleter = WTabCommand()
    }

    private fun registerListeners() {
        val manager = Bukkit.getPluginManager()
        manager.registerEvents(MessageListeners(), this)
    }

    private fun registerOther() {
        TablistManager.updateTablist()
        if(UpdateChecker.checkForUpdate(this)) {
            logger.warning("There is a new update available!")
            logger.warning("New Version: " + UpdateChecker.getLatestVersion(this))
        } else logger.info("You are using the latest version!")
        // Try to connect to BungeeCord
        ProxyMessageChannel.registerChannel()
        ProxyMessageChannel.requestData()

    }

}