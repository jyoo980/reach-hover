package com.github.jyoo980.reachhover.services

import com.github.jyoo980.reachhover.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
