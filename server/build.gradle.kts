import com.android.build.gradle.internal.tasks.factory.dependsOn

tasks {
    val check by registering {
        group = "verification"
    }

    subprojects {
        afterEvaluate {
            check.dependsOn(tasks.named("check"))
        }
    }
}
