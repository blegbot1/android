package com.zaneschepke.wireguardautotunnel.data

data class Server(
    val name: String,
    val privateKey: String,
    val publicKey: String,
    val endpoint: String,
    val allowedIPs: String = "0.0.0.0/0"
)
