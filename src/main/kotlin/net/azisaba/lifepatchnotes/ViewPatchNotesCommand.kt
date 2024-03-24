package net.azisaba.lifepatchnotes

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import kotlin.math.min

class ViewPatchNotesCommand(private val plugin: LifePatchNotes) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /viewpatchnotes <channel> [page]")
            return true
        }

        val page = args.getOrNull(1)?.toIntOrNull() ?: Integer.MAX_VALUE

        val messages = plugin.messages[args[0]] ?: run {
            return true
        }

        val actualIndex = min(page, messages.size - 1)
        sender.sendMessage("${ChatColor.AQUA}------------------------------")
        sender.sendMessage(messages[actualIndex])
        sender.sendMessage(TextComponent().apply {
            TextComponent("最初")
                .apply {
                    if (actualIndex == 0) {
                        color = ChatColor.DARK_GRAY
                    } else {
                        color = ChatColor.AQUA
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("<< 最初のページ"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lifepatchnotes:viewpatchnotes ${args[0]} 0")
                    }
                }
                .let { addExtra(it) }
            addExtra(" ")
            TextComponent("<<")
                .apply {
                    if (actualIndex == 0) {
                        color = ChatColor.DARK_GRAY
                    } else {
                        color = ChatColor.AQUA
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("<< 前のページ"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lifepatchnotes:viewpatchnotes ${args[0]} ${actualIndex - 1}")
                    }
                }
                .let { addExtra(it) }
            addExtra("§f §f §f")
            TextComponent(">>")
                .apply {
                    if (actualIndex == messages.size - 1) {
                        color = ChatColor.DARK_GRAY
                    } else {
                        color = ChatColor.AQUA
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("次のページ >>"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lifepatchnotes:viewpatchnotes ${args[0]} ${actualIndex + 1}")
                    }
                }
                .let { addExtra(it) }
            addExtra(" ")
            TextComponent("最後")
                .apply {
                    if (actualIndex == messages.size - 1) {
                        color = ChatColor.DARK_GRAY
                    } else {
                        color = ChatColor.AQUA
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("最後のページ >>"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lifepatchnotes:viewpatchnotes ${args[0]} ${messages.size - 1}")
                    }
                }
                .let { addExtra(it) }
            addExtra(" §7| ")
            TextComponent("§fページ §b${actualIndex + 1}§f/§b${messages.size}")
                .apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("クリックでページを指定"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lifepatchnotes:viewpatchnotes ${args[0]} ")
                }
                .let { addExtra(it) }
        })
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return plugin.messages.keys.filter { it.startsWith(args[0]) }
        }
        if (args.size == 2) {
            val list = plugin.messages[args[0]] ?: return emptyList()
            return list.indices.map { it.toString() }
        }
        return emptyList()
    }
}
