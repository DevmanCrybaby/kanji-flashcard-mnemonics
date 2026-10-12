package com.example.aprendakanjirapido.data

import android.content.Context
import com.example.aprendakanjirapido.model.KanjiItem
import java.io.BufferedReader
import java.io.InputStreamReader

object KanjiRepository {
    fun loadKanjis(context: Context): List<KanjiItem> {
        val list = mutableListOf<KanjiItem>()
        try {
            val inputStream = context.assets.open("tutorial_kanji_damage_componentes.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val lines = reader.readLines()
            val dataLines = if (lines.isNotEmpty() && lines[0].startsWith("Kanji")) lines.drop(1) else lines
            for (line in dataLines) {
                if (line.isBlank()) continue
                val parts = parseCsvLine(line)
                if (parts.size >= 3) {
                    list.add(
                        KanjiItem(
                            kanji = parts[0].trim(),
                            componentes = parts[1].trim(),
                            significado = parts[2].trim()
                        )
                    )
                } else if (parts.size == 2) {
                    list.add(
                        KanjiItem(
                            kanji = parts[0].trim(),
                            componentes = "",
                            significado = parts[1].trim()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (c in line) {
            when {
                c == '"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> {
                    result.add(sb.toString())
                    sb.clear()
                }
                else -> sb.append(c)
            }
        }
        result.add(sb.toString())
        return result
    }
}
