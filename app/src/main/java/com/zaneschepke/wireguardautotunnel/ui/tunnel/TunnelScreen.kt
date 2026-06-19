package com.zaneschepke.wireguardautotunnel.ui.tunnel

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.zaneschepke.wireguardautotunnel.data.AssetConfigLoader
import com.zaneschepke.wireguardautotunnel.data.Server

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TunnelScreen(context: Context) {
    val servers = remember { AssetConfigLoader.loadServers(context) }
    var selectedServer by remember { mutableStateOf<Server?>(null) }
    var isConnected by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0A0A), Color(0xFF1A1A2E), Color(0xFF0D0D1A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AnimatedContent(
                targetState = if (isConnected) "🟢 Подключено" else "🌍 Мои серверы",
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                },
                label = "header"
            ) { title ->
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
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

            AnimatedVisibility(
                visible = selectedServer != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Button(
                    onClick = { isConnected = !isConnected },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .alpha(if (isConnected) 1f else pulseAlpha),
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
    val transition = updateTransition(targetState = isSelected, label = "card")
    val cardElevation by transition.animateDp(
        transitionSpec = { tween(300) },
        label = "elevation"
    ) { if (it) 8.dp else 2.dp }

    val cardColor by transition.animateColor(
        transitionSpec = { tween(300) },
        label = "color"
    ) { if (it) Color(0xFF2A2A4A) else Color(0xFF1A1A2E) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(cardElevation, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF4CAF50) else Color.Transparent)
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
            AnimatedContent(
                targetState = if (isConnected && isSelected) "connected" else if (isSelected) "selected" else "none",
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                },
                label = "icon"
            ) { state ->
                when (state) {
                    "connected" -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Подключено",
                        tint = Color(0xFF4CAF50)
                    )
                    "selected" -> Icon(
                        Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Выбрано",
                        tint = Color(0xFFBB86FC)
                    )
                    else -> Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
