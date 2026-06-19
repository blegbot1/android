package com.zaneschepke.wireguardautotunnel.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object AssetConfigLoader {
    fun loadServers(context: Context): List<Server> {
        val servers = mutableListOf<Server>()
        try {
            val files = context.assets.list("configs") ?: return emptyList()
            for (fileName in files) {
                if (!fileName.endsWith(".conf")) continue
                val inputStream = context.assets.open("configs/$fileName")
                val reader = BufferedReader(InputStreamReader(inputStream))
                var privateKey = ""
                var publicKey = ""
                var endpoint = ""
                var currentSection = ""
                reader.forEachLine { line ->
                    val trimmed = line.trim()
                    when {
                        trimmed.startsWith("[") && trimmed.endsWith("]") -> {
                            currentSection = trimmed.substring(1, trimmed.length - 1)
                        }
                        trimmed.startsWith("PrivateKey") && currentSection == "Interface" -> {
                            privateKey = trimmed.substringAfter("=").trim()
                        }
                        trimmed.startsWith("PublicKey") && currentSection == "Peer" -> {
                            publicKey = trimmed.substringAfter("=").trim()
                        }
                        trimmed.startsWith("Endpoint") && currentSection == "Peer" -> {
                            endpoint = trimmed.substringAfter("=").trim()
                        }
                    }
                }
                if (privateKey.isNotEmpty() && publicKey.isNotEmpty() && endpoint.isNotEmpty()) {
                    val name = fileName.removeSuffix(".conf")
                    servers.add(Server(name, privateKey, publicKey, endpoint))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return servers
    }
}
