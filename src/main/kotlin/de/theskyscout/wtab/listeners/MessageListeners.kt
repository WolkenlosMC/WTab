package de.theskyscout.wtab.listeners

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.manager.GroupManager
import de.theskyscout.wtab.utils.UpdateChecker
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class MessageListeners: Listener {

    private val mm = MiniMessage.miniMessage()

    @EventHandler
    fun onMessage(event: PlayerChatEvent) {
        Bukkit.getOnlinePlayers().forEach {
            it.sendMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + event.player.displayName + "<gray> » "+ event.message))
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onJoint(event: PlayerJoinEvent) {
        event.joinMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + event.player.displayName + "<gray> » "+ "joined the game"))
        if(event.player.hasPermission("wtab.update")) {
            if(UpdateChecker.checkForUpdate(WTab.instance)) {
                event.player.sendMessage(mm.deserialize("<gray>------------------------"))
                event.player.sendMessage(mm.deserialize("${Config.prefix()} <gray>There is a new update available!"))
                event.player.sendMessage(mm.deserialize("${Config.prefix()} <gray>Download it <yellow><bold><click:open_url:https://hangar.papermc.io/TheSkyScout/WTab/versions>here</click>"))
                event.player.sendMessage(mm.deserialize("<gray>------------------------"))
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.quitMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + event.player.displayName + "<gray> » "+ "left the game"))
    }
}