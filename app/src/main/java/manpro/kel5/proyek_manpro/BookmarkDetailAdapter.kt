package manpro.kel5.proyek_manpro

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class BookmarkDetailAdapter(
    private val context: Context,
    private var bookmarks: MutableList<BookmarkDetailData>,
    private val db: FirebaseFirestore
) : BaseAdapter() {

    private var arRute = arrayListOf<Rute>()

    override fun getCount(): Int = bookmarks.size

    override fun getItem(position: Int): BookmarkDetailData = bookmarks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private lateinit var bookmark : BookmarkDetailData

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_bookmark_detail, parent, false)

        bookmark = getItem(position)
        val btnEdit = view.findViewById<ImageView>(R.id.iv_edit)
        val lokasiAsalTextView = view.findViewById<TextView>(R.id.tv_lokasi_asal)
        val lokasiTujuanTextView = view.findViewById<TextView>(R.id.tv_lokasi_tujuan)
        val labelTextView = view.findViewById<TextView>(R.id.tv_label)
        val durasiTextView = view.findViewById<TextView>(R.id.tv_durasi) // Added for duration display
        val iv_unbookmark = view.findViewById<ImageView>(R.id.iv_unbookmark)
        val btnChoose = view.findViewById<Button>(R.id.btn_choose)

        lokasiAsalTextView.text = bookmark.idStopSource
        lokasiTujuanTextView.text = bookmark.idStopDest
        labelTextView.text = bookmark.bookmarkName

        // Calculate and display duration
        fetchAndDisplayRouteDuration(bookmark.idRute, durasiTextView)
        fetchRutes()

        btnEdit.setOnClickListener {
            showEditDialog(bookmark)
        }

        iv_unbookmark.setOnClickListener {
            getItem(position)?.let { bookmark ->
                Log.d("mgmg", bookmark.docId)
                bookmarks.removeAt(position)
                notifyDataSetChanged()
                deleteBookmarkFromDatabase(bookmark.docId)
            }
        }

        btnChoose.setOnClickListener {
            Log.d("jeje ", bookmark.idRute.toString())

            val intent = Intent(context, ChooseRoute::class.java).apply {
                putExtra("kirimData", arRute)
                putExtra(ChooseRoute.asal, bookmark.idStopSource)
                putExtra(ChooseRoute.tujuan, bookmark.idStopDest)
                putExtra(ChooseRoute.index, bookmark.bookmarkName)
            }
            context.startActivity(intent)

        }



        return view
    }
    private fun fetchRutes() {
        arRute.clear()
        val validDiscoveredRoutes = bookmark.idRute // Assuming this is an ArrayList<String> containing route IDs

        val totalRoutes = validDiscoveredRoutes.size
        var counter = 0

        fun processRoute(innerList: List<String>) {
            // Initialize variables for route data
            val listId = mutableListOf<String>()
            val listAsal = mutableListOf<String>()
            val listTujuan = mutableListOf<String>()
            val listHarga = mutableListOf<Int>()
            val listDurasi = mutableListOf<Int>()
            val listJamBerangkat = mutableListOf<Int>()
            val listJamSampai = mutableListOf<Int>()
            val listTranspor = mutableListOf<String>()

            // Loop through each route ID
            for (ruteId in innerList) {
                // Fetch route data from the database
                db.collection("RuteBaru").document(ruteId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val docId = documentSnapshot.id
                            val namaAsal = documentSnapshot.getString("id_stop_source")
                            val namaTujuan = documentSnapshot.getString("id_stop_dest")
                            val jamBerangkat = documentSnapshot.getLong("jam_berangkat")?.toInt() ?: 0
                            val jamSampai = documentSnapshot.getLong("jam_sampai")?.toInt() ?: 0
                            val id_transportasi = documentSnapshot.getString("id_transportasi")
                            val durasi = documentSnapshot.getLong("estimasi")?.toInt() ?: 0
                            val biaya = documentSnapshot.getLong("price")?.toInt() ?: 0

                            // Check if all route data is present
                            if (namaAsal != null && namaTujuan != null && jamBerangkat != null && jamSampai != null && id_transportasi != null) {
                                listId.add(docId)
                                listAsal.add(namaAsal)
                                listTujuan.add(namaTujuan)
                                listHarga.add(biaya)
                                listDurasi.add(durasi)
                                listJamBerangkat.add(jamBerangkat)
                                listJamSampai.add(jamSampai)
                                listTranspor.add(id_transportasi)

                                // Build Rute object if all data is collected
                                if (listId.size == innerList.size) {
                                    arRute.add(
                                        Rute(
                                            listId,
                                            listAsal,
                                            listTujuan,
                                            listDurasi,
                                            listHarga,
                                            listJamBerangkat,
                                            listJamSampai,
                                            listTranspor
                                        )
                                    )
                                    counter++
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
        }

        // Process discovered routes
        processRoute(validDiscoveredRoutes)
    }



    private fun fetchAndDisplayRouteDuration(idRutes: List<String>, durasiTextView: TextView) {
        var totalDurationSeconds = 0

        idRutes.forEach { idRute ->
            db.collection("RuteBaru").document(idRute)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val estimasi = documentSnapshot.getLong("estimasi")
                        if (estimasi != null) {
                            totalDurationSeconds += estimasi.toInt()
                            val (hours, minutes) = convertSecondsToHoursMinutes(totalDurationSeconds)
                            if (hours > 0) {
                                durasiTextView.text = formatDuration(hours, minutes)
                            } else {
                                durasiTextView.text = "${minutes} Menit"
                            }
                            Log.d("FetchDuration", "Estimated time for $idRute: $estimasi seconds")
                        } else {
                            Log.w("FetchDuration", "Estimasi field is null for $idRute")
                        }
                    } else {
                        Log.w("FetchDuration", "Document does not exist for $idRute")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("BookmarkDetailAdapter", "Error fetching route document", e)
                }
        }
    }

    private fun convertSecondsToHoursMinutes(totalDurationSeconds: Int): Pair<Int, Int> {
        val minutes = totalDurationSeconds / 60
        val hours = minutes / 60
        return Pair(hours, minutes % 60)
    }

    private fun formatDuration(hours: Int, minutes: Int): String {
        return "${hours} Jam ${minutes} Menit"
    }


    private fun showEditDialog(bookmark: BookmarkDetailData) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_bookmark, null)
        val editTextBookmarkName = dialogView.findViewById<EditText>(R.id.editTextBookmarkName)
        editTextBookmarkName.setText(bookmark.bookmarkName)

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Edit Bookmark Name")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val newBookmarkName = editTextBookmarkName.text.toString().trim()
                if (newBookmarkName.isNotEmpty()) {
                    updateBookmarkName(bookmark.docId, newBookmarkName)
                    bookmark.bookmarkName = newBookmarkName
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Bookmark name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun updateBookmarkName(bookmarkId: String, newBookmarkName: String) {
        val bookmarkRef = db.collection("Bookmark").document(bookmarkId)

        bookmarkRef.update("bookmarkName", newBookmarkName)
            .addOnSuccessListener {
                Toast.makeText(context, "Bookmark name updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error updating bookmark name", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateData(newBookmarks: List<BookmarkDetailData>) {
        bookmarks.clear()
        bookmarks.addAll(newBookmarks)
        notifyDataSetChanged()
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
