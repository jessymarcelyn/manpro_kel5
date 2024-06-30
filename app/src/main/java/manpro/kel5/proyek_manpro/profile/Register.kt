package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.Home
import manpro.kel5.proyek_manpro.R
import manpro.kel5.proyek_manpro.databinding.ActivityRegisterBinding
import java.util.Locale

class Register : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var ti_username: TextInputEditText
    private lateinit var ti_password: TextInputEditText
    private lateinit var ti_email: TextInputEditText
    private lateinit var btn_back: ImageView
    private lateinit var btn_daftar : Button
    private lateinit var tv_login : TextView
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var autentikasi : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autentikasi = FirebaseAuth.getInstance()

        ti_username = findViewById(R.id.ti_username)
        ti_password = findViewById(R.id.ti_password)
        ti_email = findViewById(R.id.ti_email)
        btn_daftar = findViewById(R.id.button_daftar)
        btn_back = findViewById(R.id.btn_back)


        // Register
        binding.buttonDaftar.setOnClickListener {
            val username = binding.tiUsername.text.toString()
            var emailRaw = binding.tiEmail.text.toString()
            val password = binding.tiPassword.text.toString()

            val email = emailRaw.toLowerCase()
            // New User
            val newUser = hashMapOf(
                "username" to username,
                "password" to password,
                "email" to email
            )


            // Membuat user dengan username dan password
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (email.endsWith("@petra.ac.id")) {
                    Toast.makeText(this, "Email tidak boleh berakhiran @petra.ac.id", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(this, "Password harus terdiri dari minimal 6 karakter", Toast.LENGTH_SHORT).show()
                } else {
                    autentikasi.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Menambah user ke database
                            db.collection("User")
                                .add(newUser)
                                .addOnSuccessListener { documentReference ->
                                    Toast.makeText(this, "Add User Success", Toast.LENGTH_SHORT).show()
                                }

                            Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()
            }


        }

        // Back
        btn_back.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // Text : sudah punya akun ?
        // Pindah ke login page
        tv_login = findViewById(R.id.tv_login)
        tv_login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}