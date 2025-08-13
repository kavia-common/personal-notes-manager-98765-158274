package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.NoteRepository
import org.example.app.ui.adapter.NoteAdapter

class SearchFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchInput = view.findViewById(R.id.searchInput)
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = NoteAdapter { note ->
            val intent = Intent(requireContext(), NoteDetailActivity::class.java)
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
            startActivity(intent)
        }
        recycler.adapter = adapter

        searchInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                doSearch(s?.toString().orEmpty())
            }
        })
        doSearch("")
        return view
    }

    private fun doSearch(query: String) {
        val repo = NoteRepository.getInstance(requireContext())
        val results = repo.listNotes(query)
        adapter.submitList(results)
        val empty = view?.findViewById<View>(R.id.empty_view)
        empty?.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
