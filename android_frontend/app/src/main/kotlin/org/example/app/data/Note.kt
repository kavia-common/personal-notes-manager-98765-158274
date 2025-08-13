package org.example.app.data

data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val tags: List<String> = emptyList()
)
