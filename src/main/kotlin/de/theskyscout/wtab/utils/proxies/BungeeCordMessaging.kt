
package de.theskyscout.wtab.utils.proxies

import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.utils.Placeholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object BungeeCordMessaging : PluginMessageListener {

    var enabled = false

    fun registerChannel() {
        WTab.instance.server.messenger.registerOutgoingPluginChannel(WTab.instance, "BungeeCord")
        WTab.instance.server.messenger.registerIncomingPluginChannel(WTab.instance, "BungeeCord", this)
    }

    fun unregisterChannel() {
        WTab.instance.server.messenger.unregisterOutgoingPluginChannel(WTab.instance, "BungeeCord")
        WTab.instance.server.messenger.unregisterIncomingPluginChannel(WTab.instance, "BungeeCord", this)
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray?) {
        if(channel != "BungeeCord") return
        val input = ByteStreams.newDataInput(message)
        when(input.readUTF()) {
            "GetPlayerServer" -> {
                input.readUTF()
                val serverName = input.readUTF()
                Placeholders.serverName = serverName
            }
            "PlayerCount" -> {
                input.readUTF()
                val playerCount = input.readInt()
                Placeholders.playerCount = playerCount
            }
            else -> return
        }
    }

    fun sendToServer(player: Player, messages: MutableList<String>) {
        val output = ByteStreams.newDataOutput()
        for(message in messages) output.writeUTF(message)
        player.sendPluginMessage(WTab.instance, "BungeeCord", output.toByteArray())
    }

    fun requestData() {
        var runnable:BukkitTask? = null
        runnable = object : BukkitRunnable() {
            override fun run() {
                var player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null) ?: return
                sendToServer(player, mutableListOf("GetPlayerServer", player.name))
                runnable?.cancel()
                runnable = object : BukkitRunnable() {
                    override fun run() {
                        if(Placeholders.serverName == "") {
                            runnable?.cancel()
                            WTab.instance.logger.warning("Could not connect to BungeeCord/Velocity!")
                            enabled = false
                            return
                        }
                        player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null) ?: return
                        sendToServer(player, mutableListOf("PlayerCount", Placeholders.serverName))
                    }
                }.runTaskTimer(WTab.instance, 10L, 20L)
            }
        }.runTaskTimer(WTab.instance, 0L, 5L)
    }


}