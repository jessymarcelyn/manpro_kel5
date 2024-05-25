package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
    private lateinit var llLogout : LinearLayout
    private lateinit var llDelAcc : LinearLayout
    private lateinit var llChangePassword : LinearLayout
    private lateinit var text : String


    private lateinit var binding: ActivityProfileBinding
    private lateinit var autentikasi : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        BottomNav.setupBottomNavigationView(this)

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
        autentikasi = FirebaseAuth.getInstance()
        llLogout = findViewById(R.id.linearLayoutLogOut)
        llLogout.setOnClickListener {
            if(autentikasi.currentUser!= null){
                autentikasi.signOut()
                startActivity(Intent(this, Home::class.java))
                tv_usernameDisplay.text = "username"
                Toast.makeText(this, "Log Out success", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Already Logged out", Toast.LENGTH_SHORT).show()
            }
        }

        llChangePassword = findViewById(R.id.LL_change_password)
        llChangePassword.setOnClickListener{
            if(autentikasi.currentUser != null){
                startActivity(Intent(this, ChangePassword::class.java))
            }
            else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show()
            }
        }

//        Linear llayout 11 = Delete account
        llDelAcc = findViewById(R.id.linearLayout11)
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

                            tv_usernameDisplay.text = "username"
                            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }

            }else {
                Toast.makeText(this, "Log In First", Toast.LENGTH_SHORT).show()
            }
        }

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


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun getCoordinate() {
        // Provided coordinates
        db.collection("StopBaru")
            .whereEqualTo("nama","A Yani")
            .get()
            .addOnSuccessListener{querySnapshot ->
                for(document in querySnapshot){
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    latitude.toString()
                    longitude.toString()
                    text = "$latitude,$longitude"
//                    tv_usernameDisplay.text = text
                }
//                    if(!querySnapshot.isEmpty){
////                        val stop = querySnapshot.first().toObject(Stop::class.java)
//                        val latitude = querySnapshot.get
//                        val longitude = stop.longitude.toString()
//                        origins = "$latitude,$longitude"
//                    }

            }
            .addOnFailureListener {
                text = "Get Failed"
            }
    }
}