package de.theskyscout.wtab.listeners

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.config.MessagesConfig
import de.theskyscout.wtab.manager.GroupManager
import de.theskyscout.wtab.utils.UpdateChecker
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class MessageListeners: Listener {

    private val mm = MiniMessage.miniMessage()

    @EventHandler
    fun onMessage(event: AsyncChatEvent) {
        val messageContent = (event.message() as TextComponent).content()
        var message = MessagesConfig.getMessage("chat-format")
        if (message == null) message = "<gray>%player%<gray>: <white>%message%"
        message = message.replace("%player%", MessagesConfig.getNameTag(event.player))
        message = message.replace("%message%", messageContent)
        Bukkit.getOnlinePlayers().forEach {
            it.sendMessage(mm.deserialize(message))
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onJoint(event: PlayerJoinEvent) {
        var message = MessagesConfig.getMessage("join-message")
        if(message == "CANCEL") {
            event.joinMessage(null)
            return
        }
        if(message == null) message = MessagesConfig.getNameTag(event.player) + "<gray> » joined the game"
        message = message.replace("%player%", MessagesConfig.getNameTag(event.player))
        event.joinMessage(mm.deserialize(message))

        // Update Checker
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
        var message = MessagesConfig.getMessage("quit-message")
        if(message == "CANCEL") {
            event.quitMessage(null)
            return
        }
        if(message == null) message = MessagesConfig.getNameTag(event.player) + "<gray> » left the game"
        message = message.replace("%player%", MessagesConfig.getNameTag(event.player))
        event.quitMessage(mm.deserialize(message))
    }
}