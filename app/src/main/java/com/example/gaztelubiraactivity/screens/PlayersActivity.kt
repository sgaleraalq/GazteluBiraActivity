package com.example.gaztelubiraactivity.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.gaztelubiraactivity.R
import com.example.gaztelubiraactivity.SupabaseManager
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.serialization.json.Json
import android.app.Dialog
import android.widget.RadioButton


class PlayersActivity : ComponentActivity() {

    private lateinit var tlPlayerStats: TableLayout
    private lateinit var tvPositionRow: TextView
    private lateinit var tvPlayerName: TextView
    private lateinit var tvPlayerStats: TextView
    private lateinit var btnStats: Button

    private lateinit var playerStats: List<Player>

//    Sort stats dialog variables
    private lateinit var rbGoals: RadioButton
    private lateinit var rbAssists: RadioButton
    private lateinit var rbGamesPlayed: RadioButton
    private lateinit var rbProportionMatchesGoals: RadioButton
    private lateinit var rbMVP: RadioButton

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
        tvPositionRow = findViewById(R.id.tvPosition)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvPlayerStats = findViewById(R.id.tvPlayerStats)
        btnStats = findViewById(R.id.btnStats)
        tlPlayerStats = findViewById(R.id.tlPlayersStats)
    }

    private fun initListeners() {
        btnStats.setOnClickListener {
            showSortStats()
        }
    }

    private fun showSortStats(){
        val sortStatsDialog = Dialog(this)
        sortStatsDialog.setContentView(R.layout.stats_radio_group)
        initDialogComponents(sortStatsDialog)
        initDialogListeners(sortStatsDialog)
        sortStatsDialog.show()
    }

    private fun initDialogComponents(dialog: Dialog){
        rbGoals = dialog.findViewById(R.id.rbGoals)
        rbAssists = dialog.findViewById(R.id.rbAssists)
        rbGamesPlayed = dialog.findViewById(R.id.rbGamesPlayed)
        rbProportionMatchesGoals = dialog.findViewById(R.id.rbProportion)
        rbMVP = dialog.findViewById(R.id.rbMVP)
    }

    @SuppressLint("SetTextI18n")
    private fun initDialogListeners(dialog: Dialog){
        rbGoals.setOnClickListener {
            sortStats("goals")
            dialog.dismiss()
            btnStats.text = "Goals"
        }
        rbAssists.setOnClickListener {
            sortStats("assists")
            dialog.dismiss()
            btnStats.text = "Assists"
        }
        rbGamesPlayed.setOnClickListener {
            sortStats("gamesPlayed")
            dialog.dismiss()
            btnStats.text = "Matches"
        }
        rbProportionMatchesGoals.setOnClickListener {
            sortStats("proportionMatchesGoals")
            dialog.dismiss()
            btnStats.text = "Goals %"
        }
        rbMVP.setOnClickListener {
            sortStats("MVP")
            dialog.dismiss()
            btnStats.text = "MVP"
        }
    }

    private fun getData(){
        val supabaseResponse = SupabaseManager.players
        getResultsFromJson(supabaseResponse)
    }

    private fun getResultsFromJson(supabaseResponse: PostgrestResult) {
        playerStats = Json.decodeFromString<List<Player>>(supabaseResponse.body.toString()).sortedBy { it.name }

//          To get the proportion of goals per match
        updateProportions()

//          Insert all players into table
        sortStats()
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

    private fun sortStats(value: String = "goals") {
        playerStats = when (value){
            "goals" -> playerStats.sortedByDescending { it.goals }
            "assists" -> playerStats.sortedByDescending { it.assists }
            "gamesPlayed" -> playerStats.sortedByDescending { it.gamesPlayed }
            "proportionMatchesGoals" -> playerStats.sortedByDescending { it.proportionMatchesGoals }
//            TODO MVP
            else -> playerStats.sortedByDescending { it.name }
        }
        insertPlayersToTable(value)
        changeRowLabel(value)
    }

    @SuppressLint("InflateParams")
    private fun insertPlayersToTable(value: String = "goals"){
        tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
        var position = 1

        for (player in playerStats) {
            val customRowViewHolder = layoutInflater.inflate(R.layout.row_holder_view, null) as TableRow
            val tvPosition: TextView = customRowViewHolder.findViewById(R.id.tvPosition)
            val tvPlayerName: TextView = customRowViewHolder.findViewById(R.id.tvPlayerName)
            val tvPlayerStats: TextView = customRowViewHolder.findViewById(R.id.tvPlayerStats)

            when (position) {
                1 -> {
                    tvPosition.setBackgroundResource(R.drawable.golden_cell_background)
                    tvPlayerName.setBackgroundResource(R.drawable.golden_cell_background)
                    tvPlayerStats.setBackgroundResource(R.drawable.golden_cell_background)
                }
                2 -> {
                    tvPosition.setBackgroundResource(R.drawable.silver_cell_background)
                    tvPlayerName.setBackgroundResource(R.drawable.silver_cell_background)
                    tvPlayerStats.setBackgroundResource(R.drawable.silver_cell_background)
                }
                3 -> {
                    tvPosition.setBackgroundResource(R.drawable.bronze_cell_background)
                    tvPlayerName.setBackgroundResource(R.drawable.bronze_cell_background)
                    tvPlayerStats.setBackgroundResource(R.drawable.bronze_cell_background)
                }
            }

            tvPosition.text = position.toString()
            tvPlayerName.text = player.name
            tvPlayerStats.text = when (value){
                "goals" -> player.goals.toString()
                "assists" -> player.assists.toString()
                "gamesPlayed" -> player.gamesPlayed.toString()
                else -> player.proportionMatchesGoals.toString()
            }
            // Agregar la fila al TableLayout
            tlPlayerStats.addView(customRowViewHolder)
            position++
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeRowLabel(value: String = "goals") {
        when (value){
            "goals" -> tvPlayerStats.text = "Goals"
            "assists" -> tvPlayerStats.text = "Assists"
            "gamesPlayed" -> tvPlayerStats.text = "Matches"
            "proportionMatchesGoals" -> tvPlayerStats.text = "Goals %"
            else -> tvPlayerStats.text = "MVP"
        }
    }
}