package service

import java.util.*

class LoadConfig {

    fun loadConfig() : Properties{
        val prop : Properties = Properties()
        prop.load(this.javaClass.getResource("/batch.properties").openStream())

        return prop
    }
}