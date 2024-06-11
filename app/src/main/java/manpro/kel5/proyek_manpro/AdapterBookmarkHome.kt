package manpro.kel5.proyek_manpro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AdapterBookmarkHome(context: Context, private val bookmarks: List<BookmarkData>) : ArrayAdapter<BookmarkData>(context, 0, bookmarks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_bookmark_home, parent, false)

        val bookmark = getItem(position)

        val lokasiAsalTextView = view.findViewById<TextView>(R.id.tv_lokasi_asal)
        val lokasiTujuanTextView = view.findViewById<TextView>(R.id.tv_lokasi_tujuan)
        val labelTextView = view.findViewById<TextView>(R.id.tv_label)

        lokasiAsalTextView.text = bookmark?.idStopSource
        lokasiTujuanTextView.text = bookmark?.idStopDest
        labelTextView.text = "Bookmark"

        return view
    }
}
