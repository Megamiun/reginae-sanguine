tasks {
    register("check") {
        dependsOn(":app:cli:check", ":app:compose:check", ":app:viewmodel:check")
    }
}
