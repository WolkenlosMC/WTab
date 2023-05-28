package de.theskyscout.wtab.listeners

import de.theskyscout.wtab.manager.GroupManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class MessageListeners: Listener {

    private val mm = MiniMessage.miniMessage()

    @EventHandler
    fun onMessage(event: PlayerChatEvent) {
        Bukkit.getOnlinePlayers().forEach {
            it.sendMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + event.player.displayName + "<gray> » "+ event.message))
        }
        event.isCancelled = true
    }
}