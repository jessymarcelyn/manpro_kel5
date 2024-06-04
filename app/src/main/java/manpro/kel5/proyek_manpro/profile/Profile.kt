package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.BottomNav
import manpro.kel5.proyek_manpro.Home
import manpro.kel5.proyek_manpro.R
import manpro.kel5.proyek_manpro.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tv_usernameDisplay : TextView
    private lateinit var btnAddRute: Button
    private lateinit var btnAddStop : Button
    private lateinit var btnMap: Button
    private lateinit var btnLogin: Button
    private lateinit var tvAccountSetting : TextView
    private lateinit var tvLogOutDelete : TextView
    private lateinit var llLogout : LinearLayout
    private lateinit var llDelAcc : LinearLayout
    private lateinit var llChangePassword : LinearLayout
    private lateinit var llEditProfile : LinearLayout
    private lateinit var llFAQ : LinearLayout
    private lateinit var text : String





    private lateinit var binding: ActivityProfileBinding
    private lateinit var autentikasi : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        BottomNav.setupBottomNavigationView(this)

        tvAccountSetting = findViewById(R.id.textView14) // Judul Account Setting
        tvLogOutDelete = findViewById(R.id.textView23)
        llEditProfile = findViewById(R.id.linear_editprof)
        llChangePassword = findViewById(R.id.LL_change_password)
        llLogout = findViewById(R.id.linearLayout10) // Log Out
        llDelAcc = findViewById(R.id.linearLayout11) // Delete

        autentikasi = FirebaseAuth.getInstance()

        // Menghilangkan sebagian fitur profile jika belum login
        if(autentikasi.currentUser == null){
            tvAccountSetting.visibility = View.GONE
            tvLogOutDelete.visibility = View.GONE
            llEditProfile.visibility = View.GONE
            llChangePassword.visibility = View.GONE
            llDelAcc.visibility = View.GONE
            llLogout.visibility = View.GONE
        }
        else if(autentikasi.currentUser != null){
            tvAccountSetting.visibility = View.VISIBLE
            tvLogOutDelete.visibility = View.VISIBLE
            llEditProfile.visibility = View.VISIBLE
            llChangePassword.visibility = View.VISIBLE
            llDelAcc.visibility = View.VISIBLE
            llLogout.visibility = View.VISIBLE
        }

        // Add Rute
        btnAddRute = findViewById(R.id.btn_AddRute)
        btnAddRute.setOnClickListener {
            startActivity(Intent(this, AddRute::class.java))
        }

        // Add Stop
        btnAddStop = findViewById(R.id.btn_AddStop)
        btnAddStop.setOnClickListener {
            startActivity(Intent(this, AddStop::class.java))
        }

        // Register
        btnMap = findViewById(R.id.btn_register)
        btnMap.setOnClickListener {
//            startActivity(Intent(this, Map::class.java))
            startActivity(Intent(this, Register::class.java))
        }

        btnLogin = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }

        // Log Out
        llLogout.setOnClickListener {
            if(autentikasi.currentUser!= null){
                autentikasi.signOut()
                startActivity(Intent(this, Home::class.java))
                tv_usernameDisplay.text = "<Your username>"
                Toast.makeText(this, "Log Out success", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Already Logged out", Toast.LENGTH_SHORT).show()
            }
        }


        llChangePassword.setOnClickListener{
            if(autentikasi.currentUser != null){
                startActivity(Intent(this, ChangePassword::class.java))
            }
            else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show()
            }
        }

//      Delete account
        llDelAcc.setOnClickListener{
            if(autentikasi.currentUser!= null){
                val email = autentikasi.currentUser!!.email

                db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val userDocument = documents.first()
                            userDocument.reference.delete()

                            autentikasi.currentUser!!.delete()
                            startActivity(Intent(this, Home::class.java))

                            tv_usernameDisplay.text = "<Your username>"
                            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }

            }else {
                Toast.makeText(this, "Log In First", Toast.LENGTH_SHORT).show()
            }
        }

        // Menampilkan nama username
        tv_usernameDisplay = findViewById(R.id.tv_usernameDisplay)
        if(autentikasi.currentUser!= null){
            val email = autentikasi.currentUser!!.email
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.first().toObject(User::class.java)
                        val username = user.username
                        tv_usernameDisplay.text = username

                    }
                }

        }

        llFAQ = findViewById(R.id.LL_FAQ)
        llFAQ.setOnClickListener{
            startActivity(Intent(this, FAQ::class.java))
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}