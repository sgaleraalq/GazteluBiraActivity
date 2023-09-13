package com.example.gaztelubiraactivity.screens

import android.view.View
import android.widget.TextView
import com.example.gaztelubiraactivity.R

class RowViewHolder (view: View, player: Player) {

    private val view: View = view

    private var tvPlayerName: TextView = view.findViewById(R.id.tvPlayerName)
    private var tvGoals: TextView = view.findViewById(R.id.tvPlayerGoals)
    private var tvAssists: TextView = view.findViewById(R.id.tvPlayerAssists)
    private var tvMatches: TextView = view.findViewById(R.id.tvPlayerMatches)
    private var tvProportionMatchesGoals: TextView = view.findViewById(R.id.tvPlayerProportion)

    fun render(player: Player) {
        tvPlayerName.text = player.name
        tvGoals.text = player.goals.toString()
        tvAssists.text = player.assists.toString()
        tvMatches.text = player.gamesPlayed.toString()
        tvProportionMatchesGoals.text = player.proportionMatchesGoals.toString()
    }
}