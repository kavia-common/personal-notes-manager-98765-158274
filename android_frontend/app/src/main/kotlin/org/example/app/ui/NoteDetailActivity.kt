package org.example.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.appbar.MaterialToolbar
import org.example.app.R
import org.example.app.data.NoteRepository

class NoteDetailActivity : AppCompatActivity() {

    private var noteId: Long? = null
    private lateinit var titleInput: TextInputEditText
    private lateinit var contentInput: TextInputEditText
    private lateinit var tagsInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleInput = findViewById(R.id.inputTitle)
        contentInput = findViewById(R.id.inputContent)
        tagsInput = findViewById(R.id.inputTags)

        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L).takeIf { it != -1L }

        noteId?.let { id ->
            val note = NoteRepository.getInstance(this).getNote(id)
            if (note != null) {
                titleInput.setText(note.title)
                contentInput.setText(note.content)
                tagsInput.setText(note.tags.joinToString(", "))
                supportActionBar?.title = getString(R.string.title_notes)
            }
        } ?: run {
            supportActionBar?.title = getString(R.string.action_save)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_save -> {
                saveNote()
                return true
            }
            R.id.action_delete -> {
                noteId?.let {
                    NoteRepository.getInstance(this).deleteNote(it)
                }
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val content = contentInput.text?.toString()?.trim().orEmpty()
        val tags = tagsInput.text?.toString()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        if (title.isEmpty() && content.isEmpty()) {
            finish()
            return
        }

        val repo = NoteRepository.getInstance(this)
        val id = noteId
        if (id == null) {
            repo.createNote(title, content, tags)
        } else {
            repo.updateNote(id, title, content, tags)
        }
        finish()
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
