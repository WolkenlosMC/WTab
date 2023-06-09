package de.theskyscout.wtab.listeners

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
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
        val message = (event.message() as TextComponent).content()
        val name = (event.player.displayName() as TextComponent).content()
        Bukkit.getOnlinePlayers().forEach {
            it.sendMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + name + "<gray> » "+ message))
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onJoint(event: PlayerJoinEvent) {
        val name = (event.player.displayName() as TextComponent).content()
        event.joinMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + name + "<gray> » "+ "joined the game"))
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
        val name = (event.player.displayName() as TextComponent).content()
        event.quitMessage(mm.deserialize(GroupManager.getPrefix(event.player) + "<gray> | " + name + "<gray> » "+ "left the game"))
    }
}