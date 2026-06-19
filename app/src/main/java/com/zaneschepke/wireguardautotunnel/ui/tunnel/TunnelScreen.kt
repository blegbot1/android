package com.zaneschepke.wireguardautotunnel.ui.tunnel

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaneschepke.wireguardautotunnel.data.Server
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun TunnelScreen(context: Context) {
    val servers = remember { loadServersFromAssets(context) }
    var selectedServer by remember { mutableStateOf<Server?>(null) }
    var isConnected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0A0A), Color(0xFF1A1A2E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "🌍 Мои серверы",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(servers) { server ->
                    ServerCard(
                        server = server,
                        isSelected = selectedServer == server,
                        isConnected = isConnected && selectedServer == server,
                        onClick = { selectedServer = server }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedServer != null) {
                Button(
                    onClick = { isConnected = !isConnected },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) Color(0xFFE53935) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isConnected) "🔴 Отключиться" else "🟢 Подключиться",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ServerCard(
    server: Server,
    isSelected: Boolean,
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2A2A4A) else Color(0xFF1E1E2E)
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = server.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = server.endpoint,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            if (isConnected && isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Подключено",
                    tint = Color(0xFF4CAF50)
                )
            } else if (isSelected) {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Выбрано",
                    tint = Color(0xFFBB86FC)
                )
            }
        }
    }
}

fun loadServersFromAssets(context: Context): List<Server> {
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
