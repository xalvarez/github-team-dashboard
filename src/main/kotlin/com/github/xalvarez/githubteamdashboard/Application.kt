package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut.build

fun main(vararg args: String) {
    build()
        .args(*args)
        .banner(false)
        .start()
}
