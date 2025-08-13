package org.example.app.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import org.example.app.R
import org.example.app.data.NoteRepository

class TagsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_tags, container, false)
        val listView: ListView = view.findViewById(R.id.listView)
        val tags = NoteRepository.getInstance(requireContext()).listTags()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tags)
        listView.adapter = adapter
        val empty = view.findViewById<View>(R.id.empty_view)
        empty.visibility = if (tags.isEmpty()) View.VISIBLE else View.GONE
        return view
    }

    companion object {
        fun newInstance() = TagsFragment()
    }
}
