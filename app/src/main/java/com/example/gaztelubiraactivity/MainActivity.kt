package com.example.gaztelubiraactivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.gaztelubiraactivity.screens.Matches
import com.example.gaztelubiraactivity.screens.MatchesActivity
import com.example.gaztelubiraactivity.screens.PlayersActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

enum class ProviderType {
    BASIC
}

class MainActivity : ComponentActivity() {

    private lateinit var btnPlayers: Button
    private lateinit var btnMatches: Button
    private lateinit var tvPoints: TextView
    private lateinit var btnLogOut: Button
    private lateinit var tvLoggedAs: TextView
    private var points: Int = 0

    private lateinit var bundle: Bundle
    private lateinit var email: String
    private lateinit var provider: String

    private var userName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bundle = intent.extras!!
        email = bundle.getString("email").toString()
        provider = bundle.getString("provider").toString()

//        Save register data to not constantly log in
        val prefs = getSharedPreferences(getString(R.string.prefsFile), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        initComponents()
        initListeners()
        initUI()
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
//        Extract all the data from class
        runBlocking { SupabaseManager.getData() }

//        Set up Logged tag in the upper part of the screen
        if (email != "Guest") {
            getLoggedName()
        }
        if (email == "Guest") {
            tvLoggedAs.text = "Logged as: $email"
        }
//        Set up points
        getData()
    }

    @SuppressLint("SetTextI18n")
    private fun getLoggedName() {
        val userNameResponse = SupabaseManager.userAuth.body?.jsonArray

        for (user in userNameResponse!!) {
            val name = user.jsonObject["name"].toString().replace("\"","")
            val everyEmail = user.jsonObject["email"].toString().replace("\"","")
            if (everyEmail == email) {
                runOnUiThread{
                    tvLoggedAs.text = "Logged as: $name"
                }
                return
            }
        }
        runOnUiThread {
            tvLoggedAs.text = "Logged as: $email"
        }
    }

    private fun initComponents() {
        btnPlayers = findViewById(R.id.btnPlayers)
        btnMatches = findViewById(R.id.btnMatches)
        tvPoints = findViewById(R.id.tvPoints)
        btnLogOut = findViewById(R.id.btnLogOut)
        tvLoggedAs = findViewById(R.id.tvLoggedAs)
    }

    private fun initListeners() {
        btnPlayers.setOnClickListener { playersIntent() }
        btnMatches.setOnClickListener { matchesIntent() }
        btnLogOut.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefsFile), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            runBlocking { launch { signOut() }
            }
        }
    }

    private fun playersIntent() {
        val intent = Intent(this, PlayersActivity::class.java)
        startActivity(intent)
    }

    private fun matchesIntent() {
        val intent = Intent(this, MatchesActivity::class.java)
        startActivity(intent)
    }

    private fun getData() {
        val supabaseResult = SupabaseManager.games.body?.jsonArray.toString()
        getPoints(supabaseResult)
    }

    @SuppressLint("SetTextI18n")
    private fun getPoints(games: String) {
        val parsedData = Gson().fromJson(games, Array<Matches>::class.java)
        for (match in parsedData) {
            val homeTeam = match.home
            val awayTeam = match.away
            val localGoals = match.homeGoals
            val awayGoals = match.awayGoals
            if (homeTeam == "Gaztelu Bira") {
                if (localGoals > awayGoals) {
                    points += 3
                }
                if (localGoals == awayGoals) {
                    points += 1
                }
            }
            if (awayTeam == "Gaztelu Bira") {
                if (awayGoals > localGoals) {
                    points += 3
                }
                if (awayGoals == localGoals) {
                    points += 1
                }
            }
            runOnUiThread { tvPoints.text = "$points points" }
        }
    }

    private suspend fun signOut() {
        SupabaseManager.client.gotrue.logout()
        showLogInActivity()
    }

    private fun showLogInActivity() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
    }
}