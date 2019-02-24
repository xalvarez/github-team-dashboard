package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages(Application::class.java.`package`.toString())
            .mainClass(Application::class.java)
            .start()
    }
}