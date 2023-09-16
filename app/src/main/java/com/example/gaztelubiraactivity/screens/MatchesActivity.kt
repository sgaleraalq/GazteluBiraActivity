package com.example.gaztelubiraactivity.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MatchesActivity : AppCompatActivity() {

    private lateinit var tvGamesPointsWin: TextView
    private lateinit var tvGamesPointsDraw: TextView
    private lateinit var tvGamesPointsLost: TextView
    private lateinit var spinner: ProgressBar

    private lateinit var allResults: List<Matches>

    private lateinit var rvMatches: RecyclerView
    private var matchesAdapter: MatchesAdapter? = null

    private val supaBaseUrl: String = "https://onobmlegkylrcgeevzls.supabase.co"
    private val supaBaseKey: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9ub2JtbGVna3lscmNnZWV2emxzIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTQ0NTI5OTAsImV4cCI6MjAxMDAyODk5MH0.rsZu-HKj2HiZWvatYNk5_XT6ZmHDXPPc0-pQH51arqs"

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
        spinner = findViewById(R.id.pbMatches)
    }

    private fun initUI() {
        spinner.visibility = View.VISIBLE
        getData()
    }

    private fun getClient(): SupabaseClient {
        return createSupabaseClient(
            supaBaseUrl,
            supaBaseKey
        ) {
            install(Postgrest)
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            val client = getClient()
            val supabaseResponse = client.postgrest["games"].select()
            getResultsFromJson(supabaseResponse)
        }
    }

    private fun getResultsFromJson(supabaseResponse: PostgrestResult) {
        val json = Json { var ignoreUnknownKeys = true }
        try {
            allResults = json.decodeFromString(supabaseResponse.body.toString())
            matchesAdapter = MatchesAdapter(allResults)
            rvMatches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvMatches.adapter = matchesAdapter
            spinner.visibility = View.GONE
            getMatchesStats()

        } catch (e: Exception) {
            e.printStackTrace()
        }
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