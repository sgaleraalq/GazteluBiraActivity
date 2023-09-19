package com.example.gaztelubiraactivity.screens

import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.gaztelubiraactivity.BuildConfig
import com.example.gaztelubiraactivity.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class PlayersActivity : ComponentActivity() {

    private lateinit var tlPlayerStats: TableLayout

    private lateinit var btnSortName: Button
    private lateinit var btnSortGoals: Button
    private lateinit var btnSortAssists: Button
    private lateinit var btnSortGamesPlayed: Button
    private lateinit var btnSortProportionMatchesGoals: Button

    private val SupaBaseURL: String = BuildConfig.SUPABASE_URL
    private val SupaBaseKey: String = BuildConfig.SUPABASE_KEY

    private lateinit var playerStats: List<Player>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.players_activity)

        initComponents()
        initListeners()
        initUI()
    }

    private fun initUI() {
        getData()
    }

    private fun initComponents() {
        btnSortName = findViewById(R.id.btnPlayerName)
        btnSortGoals = findViewById(R.id.btnPlayerGoals)
        btnSortAssists = findViewById(R.id.btnPlayerAssists)
        btnSortGamesPlayed = findViewById(R.id.btnPlayerMatches)
        btnSortProportionMatchesGoals = findViewById(R.id.btnPlayerProportionMatchesGoals)
        tlPlayerStats = findViewById(R.id.tlPlayersStats)
    }

    private fun initListeners() {
        btnSortName.setOnClickListener { sortElements("name") }
        btnSortGoals.setOnClickListener { sortElements("goals") }
        btnSortAssists.setOnClickListener { sortElements("assists") }
        btnSortGamesPlayed.setOnClickListener { sortElements("gamesPlayed") }
        btnSortProportionMatchesGoals.setOnClickListener { sortElements("proportionMatchesGoals") }
    }

    private fun getData(){
        val client = createSupabaseClient(
            SupaBaseURL,
            SupaBaseKey
        ) {
            install(Postgrest)
        }
        lifecycleScope.launch {
            val supabaseResponse = client.postgrest["players"].select()
            getResultsFromJson(supabaseResponse)
        }
    }

    private fun getResultsFromJson(supabaseResponse: PostgrestResult) {
        val json = Json { var ignoreUnknownKeys = true }
        try {
            playerStats = json.decodeFromString(supabaseResponse.body.toString())

//            To get the proportion of goals per match
            updateProportions()

//            Insert all players into table
            insertPlayerToTable()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateProportions(){
        for (player in playerStats){
            val goals = player.goals
            val gamesPlayed = player.gamesPlayed
            val proportionMatchesGoals =
                if (gamesPlayed > 0) goals.toDouble() / gamesPlayed.toDouble() else 0.0
            player.proportionMatchesGoals = proportionMatchesGoals
        }
    }

    private fun insertPlayerToTable(){
        playerStats = playerStats.sortedBy { it.name }
        for (player in playerStats){
            val view = layoutInflater.inflate(R.layout.row_holder_view, tlPlayerStats, false)
            val rowViewHolder = RowViewHolder(view, player)
            rowViewHolder.render(player)
            tlPlayerStats.addView(view)
        }
    }

    private fun sortElements(value: String){
        when (value) {
            "name" -> {
                playerStats = playerStats.sortedBy { it.name }
                tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
                insertPlayerToTable()
            }

            "goals" -> {
                playerStats = playerStats.sortedByDescending { it.goals }
                tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
                insertPlayerToTable()
            }

            "assists" -> {
                playerStats = playerStats.sortedByDescending { it.assists }
                tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
                insertPlayerToTable()
            }

            "gamesPlayed" -> {
                playerStats = playerStats.sortedByDescending { it.gamesPlayed }
                tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
                insertPlayerToTable()
            }

            else -> {
                playerStats = playerStats.sortedByDescending { it.proportionMatchesGoals }
                tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
                insertPlayerToTable()
            }
        }
    }
}