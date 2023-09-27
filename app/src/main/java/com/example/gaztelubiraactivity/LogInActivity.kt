package com.example.gaztelubiraactivity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
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
import kotlinx.coroutines.runBlocking

@Suppress("DeferredResultUnused")
class LogInActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogIn: Button
    private lateinit var btnGuest: Button

    private lateinit var rgUserToEmail: RadioGroup
    private lateinit var btnUserToEmail: Button

    private lateinit var llLogIn: LinearLayout

    //    Set up Supabase
    private lateinit var client: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        llLogIn = findViewById(R.id.llLogIn)

//        setDefaultLanguage(this)
        session()
        initComponents()
        initListeners()
        initUI()
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefsFile), MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            llLogIn.visibility = LinearLayout.GONE
            showMain(email, ProviderType.valueOf(provider))
        }
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
        SupabaseManager.initialize()
        startClient()
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
//            Wait connectUserToEmail to finish to start logInUser() function
            connectUserToEmail()

        } catch (e: Exception) {
            showAlert(R.string.errorSignUp, R.string.userNotRegistered)
        }
    }

    private suspend fun logInUser() {
        try {
            SupabaseManager.client.gotrue.loginWith(Email) {
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
        val intent = Intent(this, StartActivity::class.java).apply {
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

        builder.setTitle(R.string.btnGuest)
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
            initUserEmailDialog(nameAndEmail)
        }
    }

    private fun initUserEmailDialog(emailList: List<UserAndEmail>) {
        val userEmailDialog = createDialogUserToEmail(emailList)

//        Initialize components and listeners
        btnUserToEmail = userEmailDialog.findViewById(R.id.btnUserEmailAccept)
        btnUserToEmail.setOnClickListener {
            runBlocking { linkNameToEmail() }
            userEmailDialog.dismiss()
        }
    }

    private fun createDialogUserToEmail(emailList: List<UserAndEmail>): Dialog {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.user_radio_group)
        rgUserToEmail = dialog.findViewById(R.id.rgUserToEmail)

        if (emailList.none { it.email == null }){
            showAlert(R.string.errorRegister, R.string.noUsersToLink)
        }
        for (user in emailList.filter { it.email == null }.sortedBy { it.name }){
            val view = RadioButton(this)
            view.text = user.name
            view.layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            val paddingInDp = 16
            val scale = resources.displayMetrics.density
            val paddingInPx = (paddingInDp * scale + 0.5f).toInt()
            view.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            view.textSize = 20f

            rgUserToEmail.addView(view)
        }

        dialog.show()
        return dialog
    }


    private suspend fun linkNameToEmail(){
        val rbSelectedUser = rgUserToEmail.findViewById<RadioButton>(rgUserToEmail.checkedRadioButtonId)
        val user = rbSelectedUser.text.toString()

        try {
            lifecycleScope.launch {
                client.postgrest["user_auth"].update(
                    {
                        set("email", etEmail.text.toString())
                    })
                {
                    eq("name", user)
                }
            }
            logInUser()
        } catch (e: Exception) {
            showAlert(R.string.errorRegister, R.string.userNotRegistered)
        }
    }
}