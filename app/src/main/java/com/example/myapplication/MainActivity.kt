package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var manager: PreferencesManager
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        manager = PreferencesManager(this)
        val intent = intent
        val first = intent.getBooleanExtra("first",true)
        if(!first){
            manager.setFirstRun()
        }
        if(manager.isFirstRun()){
            startActivity(Intent(this,WelcomeActivity::class.java))
        }

        emailEdit = findViewById(R.id.emailEdit)
        passwordEdit = findViewById(R.id.passwordEdit)

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 4000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()
            login()
        }

        val pairDeviceButton = findViewById<Button>(R.id.pairButton)
        pairDeviceButton.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

        val resetPassword = findViewById<TextView>(R.id.forgotPassword)
        resetPassword.setOnClickListener {
            if(emailEdit.text.toString().isEmpty()){
                emailEdit.error = "Enter your mail to send a password reset link"
                return@setOnClickListener
            }
            reset(emailEdit.text.toString()); emailEdit.setText(""); passwordEdit.setText("")
        }
    }

    private fun login(){

        if(emailEdit.text.toString().isEmpty()){
            emailEdit.error = "Enter email"
            emailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()){
            emailEdit.error = "Enter valid email"
            emailEdit.requestFocus()
            return
        }

        if(passwordEdit.text.toString().isEmpty()){
            passwordEdit.error = "Enter password"
            return
        }

        auth.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if(user!!.isEmailVerified){
                        startActivity(Intent(this,DevicesActivity::class.java))
                    }
                    else{
                        Toast.makeText(baseContext, "Please verify your email first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reset(email: String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check email to reset your password", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null && currentUser.isEmailVerified){ startActivity(Intent(this,DevicesActivity::class.java)) }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        exitProcess(-1)
    }
}