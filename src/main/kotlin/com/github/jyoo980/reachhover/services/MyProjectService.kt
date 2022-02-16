package com.github.jyoo980.reachhover.services

import com.intellij.openapi.project.Project
import com.github.jyoo980.reachhover.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
