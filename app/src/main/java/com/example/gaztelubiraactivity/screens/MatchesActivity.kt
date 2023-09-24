package com.example.gaztelubiraactivity.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R
import com.example.gaztelubiraactivity.SupabaseManager
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.serialization.json.Json

class MatchesActivity : AppCompatActivity() {

    private lateinit var tvGamesPointsWin: TextView
    private lateinit var tvGamesPointsDraw: TextView
    private lateinit var tvGamesPointsLost: TextView

    private lateinit var allResults: List<Matches>

    private lateinit var rvMatches: RecyclerView
    private var matchesAdapter: MatchesAdapter? = null

    private lateinit var bundle: Bundle
    private var userName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.matches_activity)

        initComponents()
        initUI()
    }
    override fun onDestroy() {
        super.onDestroy()
        matchesAdapter?.clearData()
    }

    private fun initComponents() {
        tvGamesPointsWin = findViewById(R.id.tvGamesPointsWin)
        tvGamesPointsDraw = findViewById(R.id.tvGamesPointsDraw)
        tvGamesPointsLost = findViewById(R.id.tvGamesPointsLost)
        rvMatches = findViewById(R.id.rvMatches)
    }

    private fun initUI() {
        bundle = intent.extras!!
        userName = bundle.getString("name").toString()
        getData()
    }

    private fun getData() {
        val supabaseResponse = SupabaseManager.games
        getResultsFromJson(supabaseResponse)
    }

    private fun getResultsFromJson(supabaseResponse: PostgrestResult) {
        allResults = Json.decodeFromString(supabaseResponse.body.toString())
        matchesAdapter = MatchesAdapter(allResults.sortedBy { it.id }, userName)
        rvMatches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMatches.adapter = matchesAdapter
        getMatchesStats()
    }

    private fun getMatchesStats() {
        val results = mutableMapOf("wins" to 0, "draws" to 0, "losses" to 0)

        for (match in allResults) {
            if (match.home == "Gaztelu Bira") {
                if (match.homeGoals > match.awayGoals) {
                    results["wins"] = results["wins"]!! + 1
                } else if (match.homeGoals == match.awayGoals) {
                    results["draws"] = results["draws"]!! + 1
                } else {
                    results["losses"] = results["losses"]!! + 1
                }
            } else {
                if (match.homeGoals < match.awayGoals) {
                    results["wins"] = results["wins"]!! + 1
                } else if (match.homeGoals == match.awayGoals) {
                    results["draws"] = results["draws"]!! + 1
                } else {
                    results["losses"] = results["losses"]!! + 1
                }
            }
        }
        tvGamesPointsWin.text = results["wins"].toString()
        tvGamesPointsDraw.text = results["draws"].toString()
        tvGamesPointsLost.text = results["losses"].toString()
    }
}