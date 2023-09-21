package com.example.gaztelubiraactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Suppress("DeferredResultUnused")
class LogInActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogIn: Button
    private lateinit var btnGuest: Button

    //    Set up Supabase
    private lateinit var client: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        initComponents()
        initListeners()
        initUI()
    }

    private fun initComponents() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogIn = findViewById(R.id.btnLogIn)
        btnGuest = findViewById(R.id.btnGuest)
    }

    private fun initListeners() {
        btnRegister.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    async { registerUser() }
                }
            } else {
                showAlert(R.string.errorSignUp, R.string.signUpFail)
                }
            }
        btnLogIn.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    async { logInUser() }
                }
            } else {
                showAlert(R.string.errorLogIn, R.string.logInFail)
            }
        }

        btnGuest.setOnClickListener { showMainAsGuest() }
    }

    private fun initUI() {
        SupabaseManager.initialize(this)
        startClient()
        connectUserToEmail()
    }

    private fun startClient() {
        client = SupabaseManager.client
    }

    private suspend fun registerUser() {
        try {
            client.gotrue.signUpWith(Email) {
                email = etEmail.text.toString()
                password = etPassword.text.toString()
            }
//            connectUserToEmail()
            logInUser()
        } catch (e: Exception) {
            showAlert(R.string.errorSignUp, R.string.userNotRegistered)
        }
    }

    private suspend fun logInUser() {
        try {
            client.gotrue.loginWith(Email) {
                email = etEmail.text.toString()
                password = etPassword.text.toString()
            }
            showMain(etEmail.text.toString(), ProviderType.BASIC)
        } catch (e: Exception) {
            showAlert(R.string.errorLogIn, R.string.userNotLoggedIn)
        }
    }

    private fun clearFields(){
        etPassword.text.clear()
    }
    private fun showMain(email: String, provider: ProviderType) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(intent)
    }
    private fun showAlert(errorMessage: Int, message: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(errorMessage)
        builder.setMessage(message)
        builder.setPositiveButton("Accept") { _, _ -> clearFields() }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showMainAsGuest() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.joinAsGuest)
        val combinedMessage = getString(R.string.guestMessage) + "\n\n" + getString(R.string.notAbleToVote)
        builder.setMessage(combinedMessage)

        builder.setPositiveButton(R.string.accept) { _, _ -> showMain("Guest", ProviderType.BASIC) }
        builder.setNegativeButton(R.string.cancel, null)

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun connectUserToEmail(){
        lifecycleScope.launch {
            val nameAndEmail = client.postgrest["user_auth"].select().decodeList<UserAndEmail>()
            linkNameToEmail(nameAndEmail)
        }
    }

    private fun linkNameToEmail(emailList: List<UserAndEmail>) {
        for (element in emailList.filter { it.email == null }){
            println(element)
        }
    }
}