package org.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.example.app.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar
import androidx.fragment.app.commit
import org.example.app.ui.NotesFragment
import org.example.app.ui.SearchFragment
import org.example.app.ui.TagsFragment
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        bottomNav = findViewById(R.id.bottomNav)
        fab = findViewById(R.id.fab)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, NotesFragment.newInstance())
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_notes -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, NotesFragment.newInstance())
                    }
                    toolbar.title = getString(R.string.title_notes)
                    fab.show()
                    true
                }
                R.id.menu_search -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, SearchFragment.newInstance())
                    }
                    toolbar.title = getString(R.string.title_search)
                    fab.hide()
                    true
                }
                R.id.menu_tags -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, TagsFragment.newInstance())
                    }
                    toolbar.title = getString(R.string.title_tags)
                    fab.hide()
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            val intent = Intent(this, org.example.app.ui.NoteDetailActivity::class.java)
            startActivity(intent)
        }
    }
}
