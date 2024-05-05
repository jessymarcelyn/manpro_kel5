package manpro.kel5.proyek_manpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button

class Profile : AppCompatActivity() {
    private lateinit var btnAddRute: Button
    private lateinit var btnMap: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        BottomNav.setupBottomNavigationView(this)

        btnAddRute = findViewById(R.id.btn_AddRute)
        btnAddRute.setOnClickListener {
            startActivity(Intent(this, AddRute::class.java))
        }

        btnMap = findViewById(R.id.btn_map)
        btnMap.setOnClickListener {
//            startActivity(Intent(this, Map::class.java))
            startActivity(Intent(this, login::class.java))
        }



    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}