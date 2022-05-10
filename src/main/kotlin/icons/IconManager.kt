package icons

import com.intellij.openapi.util.IconLoader

object IconManager {

    val reachabilityIcon = IconLoader.getIcon("/icons/reachability.svg", IconManager::class.java)
    val documentationIcon = IconLoader.getIcon("/icons/documentation.svg", IconManager::class.java)
    val completeTaskIcon = IconLoader.getIcon("/icons/task-complete.svg", IconManager::class.java)
    val startTaskIcon = IconLoader.getIcon("/icons/task-start.svg", IconManager::class.java)
}
