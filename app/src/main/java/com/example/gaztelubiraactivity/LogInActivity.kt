package com.example.gaztelubiraactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInActivity: AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogIn: Button

//    Set up Supabase
    private val SupaBaseUrl: String = BuildConfig.SUPABASE_URL
    private val SupaBaseKey: String = BuildConfig.SUPABASE_KEY
    private lateinit var client: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        initComponents()
        initListeners()
        initUI()
    }
    private fun initComponents(){
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogIn = findViewById(R.id.btnLogIn)
    }

    private fun initListeners(){
        val scope = CoroutineScope(Dispatchers.Main)
        btnRegister.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                scope.launch{
                    registerUser()
                }
            } else {
                showAlert()
            }
        }
        btnLogIn.setOnClickListener { logInUser() }

    }

    private fun initUI(){
        startClient()
    }

    private fun startClient() {
        client = createSupabaseClient(SupaBaseUrl, SupaBaseKey) {
            install(GoTrue)
        }
    }
    private suspend fun registerUser(){
        val user = client.gotrue.signUpWith(Email) {
            email= etEmail.text.toString()
            password = etPassword.text.toString()

        }
    }
    private fun logInUser(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Please fill all the fields")
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}