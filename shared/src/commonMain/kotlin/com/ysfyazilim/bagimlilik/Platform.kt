package com.ysfyazilim.bagimlilik

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform