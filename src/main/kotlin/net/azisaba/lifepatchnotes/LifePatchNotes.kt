package net.azisaba.lifepatchnotes

import dev.kord.core.Kord
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap

class LifePatchNotes : JavaPlugin() {
    lateinit var client: Kord
    val messages = ConcurrentHashMap<String, MutableList<String>>()
    private val thread = BotThread(this)

    override fun onEnable() {
        saveDefaultConfig()
        thread.start()
        getCommand("viewpatchnotes")?.setExecutor(ViewPatchNotesCommand(this))
    }

    override fun onDisable() {
        try {
            runBlocking {
                client.shutdown()
            }
        } finally {
            thread.interrupt()
        }
    }
}
