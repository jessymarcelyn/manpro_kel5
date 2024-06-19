package manpro.kel5.proyek_manpro

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import manpro.kel5.proyek_manpro.profile.User

class Bookmark : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private var db = FirebaseFirestore.getInstance()
    private lateinit var autentikasi : FirebaseAuth
    private lateinit var adapterBookmark: AdapterBookmarkDetail
    private lateinit var bookmarks: MutableList<BookmarkData>
    private lateinit var label_bookmark : ListView

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

        bookmarks = mutableListOf()
        label_bookmark = findViewById(R.id.label_bookmark)

        adapterBookmark = AdapterBookmarkDetail(this, bookmarks)
        label_bookmark.adapter = adapterBookmark
    }
        private fun fetchBookmarks(userId: String) {
        db.collection("Bookmark")
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                bookmarks.clear()
                for (document in documents) {
                    Log.d("uquq", "ada")
                    Log.d("uquq", "docId : "+ document.id)
                    val idRuteList = document.get("id_rute") as? List<String>
                    val idStopDest = document.getString("id_stop_dest")
                    val idStopSource = document.getString("id_stop_source")

                    if (idStopSource != null && idStopDest != null && idRuteList != null) {
                        val bookmark = BookmarkData(document.id, idStopSource, idStopDest, userId, idRuteList)
                        bookmarks.add(bookmark)
                        Log.d("uquq", "bookmarks :  " + bookmarks)
                    }
                }

                adapterBookmark.notifyDataSetChanged() // Notify adapter of changes
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error getting documents", e)
            }
    }
}