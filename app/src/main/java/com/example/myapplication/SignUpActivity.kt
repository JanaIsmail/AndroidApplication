package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button as Button

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 4000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            signUp()
        }
    }

    private fun signUp(){
        val emailSignUpEdit = findViewById<EditText>(R.id.emailSignUpEdit)
        val passwordSignUpEdit = findViewById<EditText>(R.id.passwordSignUpEdit)
        val name = findViewById<EditText>(R.id.nameEdit)
        //val passwordRetypeEdit = findViewById<EditText>(R.id.passwordRetypeEdit)

        if(name.text.toString().isEmpty()){
            name.error = "Enter your name"; name.requestFocus()
            return
        }

        if(emailSignUpEdit.text.toString().isEmpty()){
            emailSignUpEdit.error = "Enter email"; emailSignUpEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailSignUpEdit.text.toString()).matches()){
            emailSignUpEdit.error = "Enter valid email"; emailSignUpEdit.requestFocus()
            return
        }

        if(passwordSignUpEdit.text.toString().isEmpty()){
            passwordSignUpEdit.error = "Enter password"
            return
        }
//        if(passwordSignUpEdit.text.toString() != passwordRetypeEdit.text.toString()){
//            passwordRetypeEdit.error = "Passwords don't match"
//            return
//        }

        auth.createUserWithEmailAndPassword(emailSignUpEdit.text.toString(), passwordSignUpEdit.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification().addOnCompleteListener(this) {
                        if(task.isSuccessful){
                            Toast.makeText(baseContext,"Verification email sent to " + user.email!!,Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(baseContext, task.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(baseContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}