package manpro.kel5.proyek_manpro.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import manpro.kel5.proyek_manpro.Home
import manpro.kel5.proyek_manpro.R
import manpro.kel5.proyek_manpro.databinding.ActivityChangePasswordBinding

class ChangePassword : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var autentikasi: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        autentikasi = FirebaseAuth.getInstance()

        // Change Password
        binding.btnChangePassword.setOnClickListener {
            val username = binding.tiUsername.text.toString()
            val oldPassword = binding.tiOldPassword.text.toString()
            val newPassword = binding.tiNewPassword.text.toString()

            if (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                reauthenticateAndChangePassword(username, oldPassword, newPassword)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Back
        binding.btnBackChangePass.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }

    private fun reauthenticateAndChangePassword(username: String, oldPassword: String, newPassword: String) {
        val currentUser = autentikasi.currentUser

        if (currentUser != null && currentUser.email != null) {
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)
            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        currentUser.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    updateFirestorePassword(username, oldPassword, newPassword)
                                } else {
                                    Toast.makeText(this, "Error updating password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Re-authentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFirestorePassword(username: String, oldPassword: String, newPassword: String) {
        db.collection("User")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.first().toObject(User::class.java)
                    if (user.password == oldPassword) {
                        val userRef = documents.first().reference
                        userRef.update("password", newPassword)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, Home::class.java))
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
