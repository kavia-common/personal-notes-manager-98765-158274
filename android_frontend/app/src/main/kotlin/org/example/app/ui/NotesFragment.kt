package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.NoteRepository
import org.example.app.ui.adapter.NoteAdapter

class NotesFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NoteAdapter
    private var grid = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        recycler = view.findViewById(R.id.recycler)
        recycler.setHasFixedSize(true)
        setLayoutManager()
        adapter = NoteAdapter { note ->
            val intent = Intent(requireContext(), NoteDetailActivity::class.java)
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            startActivity(intent)
        }
        recycler.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        val repo = NoteRepository.getInstance(requireContext())
        val notes = repo.listNotes()
        adapter.submitList(notes)
        val empty = view?.findViewById<View>(R.id.empty_view)
        empty?.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setLayoutManager() {
        val span = if (grid) 2 else 1
        recycler.layoutManager = GridLayoutManager(requireContext(), span)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_notes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_toggle_layout) {
            grid = !grid
            setLayoutManager()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newInstance() = NotesFragment()
    }
}
