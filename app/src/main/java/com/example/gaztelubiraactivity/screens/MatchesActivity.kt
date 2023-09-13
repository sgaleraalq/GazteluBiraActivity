package com.example.gaztelubiraactivity.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class MatchesActivity : AppCompatActivity() {

    private lateinit var tvGamesPointsWin: TextView
    private lateinit var tvGamesPointsDraw: TextView
    private lateinit var tvGamesPointsLost: TextView

    private lateinit var jsonFile: String

    private lateinit var rvMatches: RecyclerView
    private lateinit var matchesAdapter: MatchesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.matches_activity)

        initComponents()
        initUI()
    }

    private fun initComponents(){
        tvGamesPointsWin = findViewById(R.id.tvGamesPointsWin)
        tvGamesPointsDraw = findViewById(R.id.tvGamesPointsDraw)
        tvGamesPointsLost = findViewById(R.id.tvGamesPointsLost)
        rvMatches = findViewById(R.id.rvMatches)
    }

    private fun initUI(){
        tvGamesPointsWin.text = "0"
        tvGamesPointsDraw.text = "0"
        tvGamesPointsLost.text = "0"
        getJson()
    }

    private fun getJson(){
        try {
            jsonFile = assets.open("players.json").bufferedReader().use { it.readText() }
            insertGames()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertGames() {
        val gson = Gson()
        val matchesObject = gson.fromJson(jsonFile, JsonObject::class.java).getAsJsonArray("matches")
        var allMatches = getGamesFromJson(matchesObject)
        matchesAdapter = MatchesAdapter(allMatches)
        rvMatches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMatches.adapter = matchesAdapter
    }

    private fun getGamesFromJson(matchesObject: JsonArray): MutableList<Matches> {
        var allMatches = mutableListOf<Matches>()
        for (match in matchesObject) {
            var newMatch = Matches(
                match.asJsonObject.get("home").asString,
                match.asJsonObject.get("away").asString,
                match.asJsonObject.get("homeGoals").asInt,
                match.asJsonObject.get("awayGoals").asInt
            )
            allMatches += newMatch
        }
        return allMatches
    }
}