package com.example.gaztelubiraactivity.screens

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesStatsViewHolder (view: View):  RecyclerView.ViewHolder(view) {

    private val cvMatchesStatsGoals: CardView = view.findViewById(R.id.cvMatchesStatsGoals)
    private val cvMatchesStatsAssits: CardView = view.findViewById(R.id.cvMatchesStatsAssits)
    private val cvMatchesStatsPlayers: CardView = view.findViewById(R.id.cvMatchesStatsPlayers)
    private val cvMatchesStatsMVP: CardView = view.findViewById(R.id.cvMatchesStatsMVP)


    fun render(matchesStats: MatchesStats, itemView: View) {
        initListeners(matchesStats)
    }

    private fun initListeners(stats: MatchesStats) {
        cvMatchesStatsGoals.setOnClickListener {
            showNonMVPDialog("Goals", stats)
        }
        cvMatchesStatsAssits.setOnClickListener {
            showNonMVPDialog("Assists", stats)
        }
        cvMatchesStatsPlayers.setOnClickListener {
            showNonMVPDialog("Players", stats)
        }
        cvMatchesStatsMVP.setOnClickListener {
            println("MVP")
        }
    }

    private fun showNonMVPDialog(context: String, stats: MatchesStats){
        val dialog = Dialog(itemView.context)
        dialog.setContentView(R.layout.dialog_stats)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_border)
        val dialogTitle = dialog.findViewById<TextView>(R.id.tvDialogStats)
        val dialogImage = dialog.findViewById<ImageView>(R.id.ivDialogStats)
        val dialogList = dialog.findViewById<ListView>(R.id.lvDialogStats)


        when (context) {
            "Goals" -> {
                dialogTitle.text = "Goals"
                dialogImage.setImageResource(R.drawable.football_ball)
                dialogList.adapter = ArrayAdapter(itemView.context, R.layout.players_in_list, stats.goals.toList()  )
            }
            "Assists" -> {
                dialogTitle.text = "Assists"
                dialogImage.setImageResource(R.drawable.football_shoe)
                dialogList.adapter = ArrayAdapter(itemView.context, R.layout.players_in_list, stats.assits.toList()  )
            }
            else -> {
                dialogTitle.text = "Players"
                dialogImage.setImageResource(R.drawable.group_players)
                dialogList.adapter = ArrayAdapter(itemView.context, R.layout.players_in_list, stats.players.sortedBy { it }.toList()  )
            }
        }
        dialog.show()
    }
}