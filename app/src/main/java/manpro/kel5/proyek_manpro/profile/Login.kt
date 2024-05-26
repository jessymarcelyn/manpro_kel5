package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
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
import manpro.kel5.proyek_manpro.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tv_lupa_pass: TextView
    private lateinit var tv_daftar: TextView
    private lateinit var ti_username: TextInputEditText
    private lateinit var ti_password: TextInputEditText
    private lateinit var button_masuk: Button

    private lateinit var binding : ActivityLoginBinding
    private lateinit var autentikasi : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ti_username = findViewById(R.id.ti_username)
        ti_password = findViewById(R.id.ti_password)
        button_masuk = findViewById(R.id.btn_masuk)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autentikasi = FirebaseAuth.getInstance()

        binding.btnMasuk.setOnClickListener {
            val username = binding.tiUsername.text.toString()
            val password = binding.tiPassword.text.toString()

            db.collection("User")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.first().toObject(User::class.java)
                        if (user.password == password) {
                            val email = user.email
                            autentikasi.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, Home::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Handle sign-in failure
                                        Toast.makeText(this, "Login failed: ${signInTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // Password does not match
                            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // User not found
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during the query
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }


        tv_daftar = findViewById(R.id.tv_daftar)
        tv_daftar.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        tv_lupa_pass = findViewById(R.id.tv_lupa_password)
        tv_lupa_pass.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return super.onOptionsItemSelected(item)
        }
    }
}