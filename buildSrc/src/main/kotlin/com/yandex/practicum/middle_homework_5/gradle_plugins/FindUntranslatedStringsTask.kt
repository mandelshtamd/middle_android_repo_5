package com.yandex.practicum.middle_homework_5.gradle_plugins

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {

    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, RESOURCES_DIR)
        val defaultStringFile = File(resDir, STRINGS_FILE_PATH)
        val defaultStrings = getStringXmlIdentities(defaultStringFile)
        val translatedDirs = resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith("values-") && file.name != "values"
        } ?: emptyArray()

        val missingNames = mutableMapOf<String, MutableList<String>>()

        translatedDirs.forEach { dir ->
            val translatedFile = File(dir, STRINGS_FILE_NAME)
            if (!translatedFile.exists()) {
                missingNames[dir.name] = defaultStrings.toMutableList()
                return@forEach
            }

            val translatedStrings = getStringXmlIdentities(translatedFile)
            val missing = defaultStrings.filterNot { it in translatedStrings }
            if (missing.isNotEmpty()) {
                missingNames[dir.name] = missing.toMutableList()
            }
        }

        if (missingNames.isNotEmpty()) {
            throw GradleException(buildErrorText(missingNames))
        }
    }

    private fun getStringXmlIdentities(file: File): List<String> {
        if (!file.exists()) return emptyList()

        val nodeList: NodeList = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(file)
            .getElementsByTagName("string")

        return (0 until nodeList.length).mapNotNull { i ->
            nodeList.item(i).attributes?.getNamedItem("name")?.nodeValue
        }
    }

    private fun buildErrorText(missingTranslations: Map<String, List<String>>): String {
        return buildString {
            append("Missing translations found:\n")
            missingTranslations.forEach { (lang, names) ->
                append("\n=== In $lang ===\n")
                names.joinTo(this, separator = "\n")
            }
            append("\n\nPlease add the missing strings to the corresponding files.")
        }
    }

    private companion object {
        private const val RESOURCES_DIR = "src/main/res"
        private const val STRINGS_FILE_NAME = "strings.xml"
        private const val STRINGS_FILE_PATH = "values/$STRINGS_FILE_NAME"
    }
}
