
package de.theskyscout.wtab.utils.proxies

import com.google.common.collect.Iterables
import com.google.common.io.ByteStreams
import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.utils.Placeholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import org.bukkit.scheduler.BukkitRunnable

object ProxyMessageChannel : PluginMessageListener {


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
            "GetServer" -> {
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
        var counter = 0
        object : BukkitRunnable() {
            override fun run() {
                val player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null) ?: return
                if(counter >= 1 && Placeholders.serverName == "") {
                    WTab.instance.logger.warning("Could not connect to BungeeCord!")
                    cancel()
                    return
                }
                sendToServer(player, mutableListOf("GetServer", "GetServer"))
                sendToServer(player, mutableListOf("PlayerCount", "ALL"))
                counter++
            }
        }.runTaskTimer(WTab.instance, 0, 20)
    }


}