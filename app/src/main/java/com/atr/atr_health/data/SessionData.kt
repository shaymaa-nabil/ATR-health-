package com.atr.atr_health.data

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SessionData(
    dirPath: String,
    dataContainer: HashMap<String, Double>
) {
    private val file: File

    init {
        // set the file path
        val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_h_mm_ss")
        file = File("${dirPath}/${LocalDateTime.now().format(formatter)}.csv")

        // set the file header
        var header = "timestamp"
        for (container in dataContainer) {
            header += ", ${container.key}"
        }
        file.writeText(header + "\n")
    }

    fun appendDatapoint(dataPoint: HashMap<String, Double>) {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ss a")
        var entry = LocalDateTime.now().format(formatter)
        for (point in dataPoint) {
            entry += ", ${point.value}"
        }
        file.appendText(entry + "\n")
    }

    fun getFile(): File {
        return file
    }
}