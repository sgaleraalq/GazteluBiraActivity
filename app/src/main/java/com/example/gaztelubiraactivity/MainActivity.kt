package com.example.gaztelubiraactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.gaztelubiraactivity.screens.MatchesActivity
import com.example.gaztelubiraactivity.screens.PlayersActivity
import com.google.gson.Gson
import com.google.gson.JsonObject


class MainActivity : ComponentActivity() {

    private lateinit var btnPlayers: Button
    private lateinit var btnMatches: Button
    private lateinit var tvPoints: TextView
    private var points: Int = 0

    private lateinit var jsonFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initListeners()
        initUI()
    }

    private fun initUI(){
        getJson()
        getPoints()
    }

    private fun initComponents(){
        btnPlayers = findViewById(R.id.btnPlayers)
        btnMatches = findViewById(R.id.btnMatches)
        tvPoints = findViewById(R.id.tvPoints)
    }

    private fun initListeners(){
        btnPlayers.setOnClickListener { playersIntent() }
        btnMatches.setOnClickListener { matchesIntent() }
    }

    private fun playersIntent(){
        val intent = Intent(this, PlayersActivity::class.java)
        startActivity(intent)
    }

    private fun matchesIntent(){
        val intent = Intent(this, MatchesActivity::class.java)
        startActivity(intent)
    }

    private fun getJson(){
        try {
            jsonFile = assets.open("players.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getPoints(){
        val gson = Gson()
        val jsonFile = gson.fromJson(jsonFile, JsonObject::class.java).getAsJsonArray("matches")

        for (match in jsonFile){
            val homeTeam = match.asJsonObject.get("home").asString
            val awayTeam = match.asJsonObject.get("away").asString
            val localGoals = match.asJsonObject.get("homeGoals").asInt
            val awayGoals = match.asJsonObject.get("awayGoals").asInt

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
        }
        tvPoints.text = "$points points"
    }

}