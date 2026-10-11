package com.example.aprendakanjirapido.model

data class KanjiItem(
    val kanji: String,
    val componentes: String,
    val significado: String,
    var mnemonic: String = "",
    var failCount: Int = 0
)
