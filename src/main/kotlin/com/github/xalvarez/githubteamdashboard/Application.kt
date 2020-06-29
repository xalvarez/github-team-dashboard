package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("com.github.xalvarez.githubteamdashboard")
            .start()
    }
}