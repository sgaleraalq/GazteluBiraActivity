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
import kotlin.collections.listOf as listOf1

class MatchesActivity : AppCompatActivity() {

    private lateinit var tvGamesPointsWin: TextView
    private lateinit var tvGamesPointsDraw: TextView
    private lateinit var tvGamesPointsLost: TextView

    private lateinit var jsonFile: String
    private lateinit var allResults: MutableList<Matches>

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
        getJson()
        val results = getMatchesStats()
        tvGamesPointsWin.text = results["wins"].toString()
        tvGamesPointsDraw.text = results["draws"].toString()
        tvGamesPointsLost.text = results["losses"].toString()
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
        allResults = mutableListOf()
        for (match in matchesObject) {
            var goals = listOf1("Gorka", "Oso", "Nando")
            var assits = listOf1("Garru", "Xabi")
            var players = listOf1("Gorka", "Oso", "Nando", "Garru", "Xabi", "Haaland", "Dani", "Jon")
            var newMatch = Matches(
                match.asJsonObject.get("home").asString,
                match.asJsonObject.get("away").asString,
                match.asJsonObject.get("homeGoals").asInt,
                match.asJsonObject.get("awayGoals").asInt,
                MatchesStats(goals, assits, players.sortedBy { it }, "Haaland")
            )
            allResults += newMatch
        }
        return allResults
    }

    private fun getMatchesStats(): MutableMap<String, Int> {
        var results = mutableMapOf("wins" to 0, "draws" to 0, "losses" to 0)

        for (match in allResults){
            if (match.local == "Gaztelu Bira") {
                if (match.localGoals > match.visitorGoals) {
                    results["wins"] = results["wins"]!! + 1
                } else if (match.localGoals == match.visitorGoals) {
                    results["draws"] = results["draws"]!! + 1
                } else {
                    results["losses"] = results["losses"]!! + 1
                }
            } else {
                if (match.localGoals < match.visitorGoals) {
                    results["wins"] = results["wins"]!! + 1
                } else if (match.localGoals == match.visitorGoals) {
                    results["draws"] = results["draws"]!! + 1
                } else {
                    results["losses"] = results["losses"]!! + 1
                }
            }
        }
        return results
    }
}