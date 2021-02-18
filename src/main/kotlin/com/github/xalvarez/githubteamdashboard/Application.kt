package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.github.xalvarez.githubteamdashboard")
        .banner(false)
        .start()
}
