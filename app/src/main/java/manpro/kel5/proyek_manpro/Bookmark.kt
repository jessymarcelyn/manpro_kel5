package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import manpro.kel5.proyek_manpro.profile.User

class Bookmark : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private var db = FirebaseFirestore.getInstance()
    private lateinit var autentikasi : FirebaseAuth
    private lateinit var adapterBookmark: BookmarkDetailAdapter
    private lateinit var bookmarks: List<BookmarkDetailData>
    private lateinit var label_bookmark : ListView
    private lateinit var dataAsal: String
    private lateinit var dataTujuan: String
    private lateinit var tanggalDate: String
    private lateinit var btn_edit: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookmark)

        autentikasi = FirebaseAuth.getInstance()
        db = Firebase.firestore
        if (autentikasi.currentUser != null) {
            val email = autentikasi.currentUser!!.email
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.first().toObject(User::class.java)
                        val usrname = user.username

                        Log.d("uquq", "username : " + usrname)
                        fetchBookmarks(usrname)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("kuku", "Error getting user document", e)
                }
        }
        dataAsal = intent.getStringExtra(SelectRute.asal) ?: ""
        dataTujuan = intent.getStringExtra(SelectRute.tujuan) ?: ""
        tanggalDate = intent.getStringExtra(SelectRute.tanggal) ?: ""
        val username = intent.getStringExtra(SelectRute.username) ?: ""

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.putExtra(Home.dataAsall, dataAsal)
            intent.putExtra(Home.dataTujuann, dataTujuan)
            intent.putExtra(Home.tanggal, tanggalDate)
            intent.putExtra(Home.username, username)
            startActivity(intent)
        }

        bookmarks = mutableListOf()
        label_bookmark = findViewById(R.id.label_bookmark)

        adapterBookmark = BookmarkDetailAdapter(this,
            bookmarks as MutableList<BookmarkDetailData>, db)
        label_bookmark.adapter = adapterBookmark

    }
    private fun fetchBookmarks(userId: String) {
        db.collection("Bookmark")
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                val newBookmarks = mutableListOf<BookmarkDetailData>()
                for (document in documents) {
                    val idRuteList = document.get("id_rute") as? List<String>
                    val idStopDest = document.getString("id_stop_dest")
                    val idStopSource = document.getString("id_stop_source")
                    val bookmarkName = document.getString("bookmarkName")

                    if (idStopSource != null && idStopDest != null && idRuteList != null) {
                        val bookmark = BookmarkDetailData(
                            document.id,
                            idStopSource,
                            idStopDest,
                            userId,
                            idRuteList,
                            bookmarkName ?: ""
                        )
                        newBookmarks.add(bookmark)
                    }
                }

                bookmarks = newBookmarks
                adapterBookmark.updateData(newBookmarks)
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error getting documents", e)
            }
    }
}