package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import br.com.gabryel.reginaesanguine.app.ui.components.TransitionType.ENTER
import br.com.gabryel.reginaesanguine.app.ui.components.TransitionType.EXIT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

typealias NavComposable<T> = @Composable context (NavigationManager<T>)
AnimatedVisibilityScope.() -> Unit

enum class TransitionType {
    ENTER,
    EXIT
}

@Composable
fun <T> InstanceNavigationStack(start: T, durationMillis: Int = 150, build: InstanceGraphBuilder<T>.() -> Unit) {
    val graph = remember { InstanceGraphBuilder<T>().also(build) }
    val navigationManager = remember { NavigationManager(start) }
    val current by navigationManager.current.collectAsState()
    val transitionType by navigationManager.transitionType.collectAsState()

    context(navigationManager) {
        graph.routes.forEach { (key, screen) ->
            AnimatedVisibility(
                key == current.last(),
                Modifier.fillMaxSize(),
                enter = slideInVertically(initialOffsetY = {
                    if (transitionType == ENTER) -it else it
                }, animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)),
                exit = slideOutVertically(targetOffsetY = {
                    if (transitionType == EXIT) -it else it
                }, animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)),
            ) {
                screen()
            }
        }
    }
}

class InstanceGraphBuilder<T>(val routes: MutableMap<T, NavComposable<T>> = mutableMapOf()) {
    fun addRoute(target: T, screen: NavComposable<T>) {
        routes[target] = screen
    }
}

class NavigationManager<T>(initialState: T) {
    private val stack = MutableStateFlow(listOf(initialState))

    private val lastTransitionType = MutableStateFlow(ENTER)

    val current = stack.asStateFlow()

    val transitionType = lastTransitionType.asStateFlow()

    fun push(target: T) {
        stack.update { it + target }
        lastTransitionType.update { ENTER }
    }

    fun pop() {
        stack.update { it.dropLast(1) }
        lastTransitionType.update { EXIT }
    }
}
