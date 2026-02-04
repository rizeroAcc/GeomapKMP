package com.rizero.geomapkmp

interface Platform {
    val name: String
}

 expect fun getPlatform(): Platform