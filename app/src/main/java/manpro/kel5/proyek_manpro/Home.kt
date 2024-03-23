package manpro.kel5.proyek_manpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.BottomNav.Companion.setupBottomNavigationView

class Home : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigationView(this)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}