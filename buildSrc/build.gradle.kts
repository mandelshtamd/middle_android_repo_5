plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("findUntranslatedStrings") {
            id = "findUntranslatedStrings"
            implementationClass = "com.yandex.practicum.middle_homework_5.gradle_plugins.FindUntranslatedStringsPlugin"
        }
    }
}
