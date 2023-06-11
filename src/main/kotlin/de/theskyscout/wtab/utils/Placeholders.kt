package de.theskyscout.wtab.utils

import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

object Placeholders {

    var serverName = ""
    var playerCount = 0

    fun replaceInString(string: String, player: Player): String {
        return string
            .replace("%player%", player.name)
            .replace("%p%", player.name)
            .replace("%player_displayname%", (player.displayName() as TextComponent).content())
            .replace("%p_d%", (player.displayName() as TextComponent).content())
            .replace("%online_players%", player.server.onlinePlayers.size.toString())
            .replace("%o_p%", player.server.onlinePlayers.size.toString())
            .replace("%max_players%", player.server.maxPlayers.toString())
            .replace("%m_p%", player.server.maxPlayers.toString())
            .replace("%server_name%", serverName)
            .replace("%<server_name%", serverName.replaceFirstChar { it.uppercase() })
            .replace("%s_n%", serverName)
            .replace("%<s_n%", serverName.replaceFirstChar { it.uppercase() })
            .replace("%proxy_online_players%", playerCount.toString())
            .replace("%p_o_p%", playerCount.toString())
    }
}