package manpro.kel5.proyek_manpro.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import java.net.URLEncoder

class Profile : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tv_usernameDisplay : TextView
    private lateinit var tvAddStopRute : TextView
    private lateinit var llAddRute: LinearLayout
    private lateinit var llAddStop : LinearLayout
    private lateinit var btnMap: Button
    private lateinit var btnLogin: Button
    private lateinit var tvAccountSetting : TextView
    private lateinit var tvLogOutDelete : TextView
    private lateinit var llLogout : LinearLayout
    private lateinit var llDelAcc : LinearLayout
    private lateinit var llChangePassword : LinearLayout
    private lateinit var llEditProfile : LinearLayout
    private lateinit var llFAQ : LinearLayout
    private lateinit var llCustomerService : LinearLayout
    private lateinit var text : String

    private lateinit var binding: ActivityProfileBinding
    private lateinit var autentikasi : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        BottomNav.setupBottomNavigationView(this)

        tvAccountSetting = findViewById(R.id.textView14) // Judul Account Setting
        llChangePassword = findViewById(R.id.LL_change_password)
        tvLogOutDelete = findViewById(R.id.textView23)
        llEditProfile = findViewById(R.id.linear_editprof)

        llLogout = findViewById(R.id.linearLayout10) // Log Out
        llDelAcc = findViewById(R.id.linearLayout11) // Delete

        // Add Stop Rute
        tvAddStopRute = findViewById(R.id.tv_judul_add_stop_rute)
        llAddStop = findViewById(R.id.linearLayoutAddStop)
        llAddRute = findViewById(R.id.linearLayoutAddRute)

        autentikasi = FirebaseAuth.getInstance()

        // Menghilangkan sebagian fitur profile jika belum login
        if(autentikasi.currentUser == null){
            logOutSetGone()
        }
        // user telah login
        else if(autentikasi.currentUser != null){
            val email_user = autentikasi.currentUser!!.email
            if (email_user != null) {
                // ADMIN
                // Admin --> username : admin, password : admin123
                if(email_user.endsWith("@petra.ac.id")){
                    logInAdminSetVisible()
                }
                // USER
                else if (email_user.endsWith("@gmail.com")){
                    logInUserSetVisible()
                }
            }
        }

        // Add Rute
        llAddRute.setOnClickListener {
            startActivity(Intent(this, AddRute::class.java))
        }

        // Add Stop
        llAddStop.setOnClickListener {
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
                tv_usernameDisplay.text = "'Your username'"
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

                            tv_usernameDisplay.text = "'Your username'"
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

        // FAQ
        llFAQ = findViewById(R.id.LL_FAQ)
        llFAQ.setOnClickListener{
            startActivity(Intent(this, FAQ::class.java))
        }

        // Customer Service
        llCustomerService = findViewById(R.id.linearLayout_customer_service)
        llCustomerService.setOnClickListener{
            val phoneNumber = "+6281231808851"
            val message = "Hello, I need help with..."

            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${URLEncoder.encode(message, "UTF-8")}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            try {
                intent.setPackage("com.whatsapp")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // WhatsApp not installed, fallback to another method
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun logOutSetGone(){
        tvAccountSetting.visibility = View.GONE
        llEditProfile.visibility = View.GONE
        llChangePassword.visibility = View.GONE

        tvAddStopRute.visibility = View.GONE
        llAddStop.visibility = View.GONE
        llAddRute.visibility = View.GONE

        tvLogOutDelete.visibility = View.GONE
        llLogout.visibility = View.GONE
        llDelAcc.visibility = View.GONE

    }

    fun logInAdminSetVisible(){
        tvAccountSetting.visibility = View.GONE
        llEditProfile.visibility = View.GONE
        llChangePassword.visibility = View.GONE

        tvAddStopRute.visibility = View.VISIBLE
        llAddStop.visibility = View.VISIBLE
        llAddRute.visibility = View.VISIBLE

        tvLogOutDelete.visibility = View.VISIBLE
        llLogout.visibility = View.VISIBLE
        llDelAcc.visibility = View.GONE
    }

    fun logInUserSetVisible(){
        tvAccountSetting.visibility = View.VISIBLE
        llEditProfile.visibility = View.VISIBLE
        llChangePassword.visibility = View.VISIBLE

        tvAddStopRute.visibility = View.GONE
        llAddStop.visibility = View.GONE
        llAddRute.visibility = View.GONE

        tvLogOutDelete.visibility = View.VISIBLE
        llLogout.visibility = View.VISIBLE
        llDelAcc.visibility = View.VISIBLE
    }
}