package com.example.kiddobyte.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kiddobyte.R
import com.example.kiddobyte.databinding.ActivityRegisterBinding
import com.example.kiddobyte.teacher.TeacherActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.createButton.setOnClickListener {
            createUser()


        }
    }
private fun createUser(){
    val email = binding.emailInput.text.toString()
    val password = binding.passwordInput.text.toString()
    val name = binding.nameInput.text.toString()


    if(email.isEmpty()||password.isEmpty()||name.isEmpty()){
        Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_LONG).show()
        return
    }
    if(email.isNotEmpty() && password.isNotEmpty()){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                val user = firebaseAuth.currentUser
                user?.sendEmailVerification()
                user?.let{
                    val uid = user.uid
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "userType" to "Parent",
                    )
                    firestore.collection("users").document(uid).set(userData)
                        .addOnSuccessListener {
                            user.sendEmailVerification()
                            Log.d("Firestore success", "USER data saved successfully")
                            Toast.makeText(this, "A verification link has been sent to your email account.", Toast.LENGTH_SHORT).show()

                            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putString("userType", "Parent")
                            editor.apply()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener{
                            Log.w("Firestore error", "Error adding user", it)
                            Toast.makeText(this, "Error registering! Please try again later", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

}