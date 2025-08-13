package org.example.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class NoteRepository private constructor(private val dbHelper: NotesDbHelper) {

    fun listNotes(query: String? = null): List<Note> {
        val db = dbHelper.readableDatabase
        val selection: String?
        val selectionArgs: Array<String>?
        if (!query.isNullOrBlank()) {
            selection = "title LIKE ? OR content LIKE ?"
            val q = "%$query%"
            selectionArgs = arrayOf(q, q)
        } else {
            selection = null
            selectionArgs = null
        }
        val cursor = db.query("notes", arrayOf("id", "title", "content", "created_at", "updated_at"),
            selection, selectionArgs, null, null, "updated_at DESC")
        val notes = mutableListOf<Note>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                val note = Note(
                    id = id,
                    title = it.getString(1),
                    content = it.getString(2),
                    createdAt = it.getLong(3),
                    updatedAt = it.getLong(4),
                    tags = getTagsForNote(id)
                )
                notes.add(note)
            }
        }
        return notes
    }

    fun getNote(id: Long): Note? {
        val db = dbHelper.readableDatabase
        val cursor = db.query("notes", arrayOf("id", "title", "content", "created_at", "updated_at"),
            "id=?", arrayOf(id.toString()), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return Note(
                    id = it.getLong(0),
                    title = it.getString(1),
                    content = it.getString(2),
                    createdAt = it.getLong(3),
                    updatedAt = it.getLong(4),
                    tags = getTagsForNote(id)
                )
            }
        }
        return null
    }

    fun createNote(title: String, content: String, tags: List<String>): Long {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
            put("created_at", now)
            put("updated_at", now)
        }
        val db = dbHelper.writableDatabase
        val id = db.insert("notes", null, values)
        setTagsForNote(id, tags)
        return id
    }

    fun updateNote(id: Long, title: String, content: String, tags: List<String>) {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
            put("updated_at", now)
        }
        val db = dbHelper.writableDatabase
        db.update("notes", values, "id=?", arrayOf(id.toString()))
        setTagsForNote(id, tags)
    }

    fun deleteNote(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete("note_tags", "note_id=?", arrayOf(id.toString()))
        db.delete("notes", "id=?", arrayOf(id.toString()))
    }

    fun listTags(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("tags", arrayOf("name"), null, null, null, null, "name ASC")
        val tags = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                tags.add(it.getString(0))
            }
        }
        return tags
    }

    private fun getTagsForNote(noteId: Long): List<String> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT t.name 
            FROM tags t
            JOIN note_tags nt ON nt.tag_id = t.id
            WHERE nt.note_id = ?
            ORDER BY t.name ASC
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(noteId.toString()))
        val tags = mutableListOf<String>()
        cursor.use {
            while (it.moveToNext()) {
                tags.add(it.getString(0))
            }
        }
        return tags
    }

    private fun setTagsForNote(noteId: Long, tags: List<String>) {
        val db = dbHelper.writableDatabase
        db.delete("note_tags", "note_id=?", arrayOf(noteId.toString()))
        val clean = tags.mapNotNull { it.trim().lowercase().takeIf { s -> s.isNotBlank() } }.distinct()
        clean.forEach { name ->
            val tagId = getOrCreateTagId(name)
            val values = ContentValues().apply {
                put("note_id", noteId)
                put("tag_id", tagId)
            }
            db.insertWithOnConflict("note_tags", null, values, android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun getOrCreateTagId(name: String): Long {
        val db = dbHelper.writableDatabase
        val cursor: Cursor = db.query("tags", arrayOf("id"), "name=?", arrayOf(name), null, null, null)
        cursor.use {
            if (it.moveToFirst()) return it.getLong(0)
        }
        val values = ContentValues().apply { put("name", name) }
        return db.insert("tags", null, values)
    }

    companion object {
        @Volatile private var INSTANCE: NoteRepository? = null

        fun getInstance(context: Context): NoteRepository {
            return INSTANCE ?: synchronized(this) {
                val inst = NoteRepository(NotesDbHelper(context.applicationContext))
                INSTANCE = inst
                inst
            }
        }
    }
}
