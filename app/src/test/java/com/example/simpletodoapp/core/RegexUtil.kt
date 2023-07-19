package com.example.simpletodoapp.core

val String.regexPatternForSearching: String
    get() = split(' ').joinToString("") { word ->
        "^(?=.*\\b$word\\b)"
    }