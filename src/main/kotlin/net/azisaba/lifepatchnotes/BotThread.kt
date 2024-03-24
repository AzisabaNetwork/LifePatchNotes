package net.azisaba.lifepatchnotes

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import java.util.*

class BotThread(private val plugin: LifePatchNotes) : Thread("LifePatchNotes-Bot-Thread") {
    @OptIn(PrivilegedIntent::class)
    override fun run() {
        runBlocking {
            plugin.client = Kord(plugin.config.getString("token") ?: "")

            plugin.client.on<ReadyEvent> {
                plugin.slF4JLogger.info("Logged in as ${plugin.client.getSelf().username} (ID: ${plugin.client.getSelf().id})")
                plugin.config.getStringList("channels").forEach { channelId ->
                    try {
                        val channel = plugin.client.getChannelOf<MessageChannel>(Snowflake(channelId)) ?: error("Channel not found")
                        val list = Collections.synchronizedList(mutableListOf<String>())
                        channel.getMessagesBefore(Snowflake.max, 100).collect {
                            var content = it.content
                            if (it.attachments.isNotEmpty()) content += "\n"
                            it.attachments.forEach { at -> content += "${at.url}\n" }
                            list.add(content)
                        }
                        plugin.messages[channel.data.name.value!!] = list.reversed().toMutableList()
                        plugin.slF4JLogger.info("Fetched ${list.size} messages from $channelId")
                    } catch (e: Exception) {
                        plugin.slF4JLogger.error("Failed to fetch messages from $channelId", e)
                    }
                }
            }

            plugin.client.on<MessageCreateEvent> {
                if (message.author?.isBot != false) return@on
                if (message.channelId.toString() !in plugin.config.getStringList("channels")) return@on
                var content = message.content
                if (message.attachments.isNotEmpty()) content += "\n"
                message.attachments.forEach { content += "${it.url}\n" }
                plugin.slF4JLogger.info("Received message from ${message.author!!.username}:\n$content")
                plugin.messages.computeIfAbsent(message.channel.fetchChannel().data.name.value!!) {
                    Collections.synchronizedList(mutableListOf())
                }.add(content)
                // broadcast message
                Bukkit.broadcastMessage(content)
            }

            plugin.slF4JLogger.info("Logging in...")
            plugin.client.login {
                intents {
                    +Intents.NON_PRIVILEGED
                    +Intent.MessageContent
                }
            }
        }
    }
}
