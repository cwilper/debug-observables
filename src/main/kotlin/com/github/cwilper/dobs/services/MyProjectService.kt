package com.github.cwilper.dobs.services

import com.github.cwilper.dobs.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
