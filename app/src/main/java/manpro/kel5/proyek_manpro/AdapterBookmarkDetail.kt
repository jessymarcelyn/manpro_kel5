package manpro.kel5.proyek_manpro

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class AdapterBookmarkDetail(context: Context, private var bookmarks: MutableList<BookmarkData>) : ArrayAdapter<BookmarkData>(context, 0, bookmarks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_bookmark_detail, parent, false)

        val bookmark = getItem(position)

        val lokasiAsalTextView = view.findViewById<TextView>(R.id.tv_lokasi_asal)
        val lokasiTujuanTextView = view.findViewById<TextView>(R.id.tv_lokasi_tujuan)
        val labelTextView = view.findViewById<TextView>(R.id.tv_label)
        val iv_unbookmark = view.findViewById<ImageView>(R.id.iv_unbookmark)

        lokasiAsalTextView.text = bookmark?.idStopSource
        lokasiTujuanTextView.text = bookmark?.idStopDest
        labelTextView.text = bookmark?.bookmarkName

        iv_unbookmark.setOnClickListener {
            getItem(position)?.let { bookmark ->
                Log.d("mgmg", bookmark.docId)
                remove(bookmark)
                notifyDataSetChanged()
                deleteBookmarkFromDatabase(bookmark.docId)
            }
        }

//        val btn_view = view.findViewById<Button>(R.id.btn_view)


        return view
    }

    private fun deleteBookmarkFromDatabase(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Bookmark").document(documentId)
            .delete()
            .addOnSuccessListener {
                // Handle success
                Log.d("AdapterBookmarkDetail", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w("AdapterBookmarkDetail", "Error deleting document", e)
            }
    }
}

