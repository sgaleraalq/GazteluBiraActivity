//package com.example.gaztelubiraactivity.screens
//
//import android.view.View
//import android.widget.TextView
//import com.example.gaztelubiraactivity.R
//
//class RowViewHolder (view: View, player: Player) {
//
//    private val view: View = view
//
//    private var tvPosition: TextView = view.findViewById(R.id.tvPosition)
//    private var tvPlayerName: TextView = view.findViewById(R.id.tvPlayerName)
//    private var tvStats: TextView = view.findViewById(R.id.tvPlayerStats)
//
//    fun render(player: Player) {
//        tvPlayerName.text = player.name
//        tvGoals.text = player.goals.toString()
//        tvAssists.text = player.assists.toString()
//        tvMatches.text = player.gamesPlayed.toString()
//        tvProportionMatchesGoals.text = player.proportionMatchesGoals.toString()
//    }
//}