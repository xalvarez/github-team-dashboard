package com.github.xalvarez.githubteamdashboard

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.views.View

@Controller
class ErrorController {

    @Error(global = true)
    @View("error")
    fun configurationError(): HttpResponse<Any> {
        val model = HashMap<String, Any>()
        model["error"] = ErrorModel(
            title = "Configuration error", message = """
            Please have a look at the project's
            <a href="https://github.com/xalvarez/github-team-dashboard/blob/master/README.md" target="_blank">README</a>
            file or <a href="https://github.com/xalvarez/github-team-dashboard/issues" target="_blank">write an
            issue</a> if you found a bug.
        """.trimIndent()
        )

        return HttpResponse.ok(model)
    }
}