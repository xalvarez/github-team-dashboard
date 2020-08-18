package com.github.xalvarez.githubteamdashboard

import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build()
        .args(*args)
        .packages("com.github.xalvarez.githubteamdashboard")
        .start()
}
