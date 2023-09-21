package com.example.gaztelubiraactivity.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesStatsAdapter(private val matchesStats: MatchesStats) :
    RecyclerView.Adapter<MatchesStatsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.matches_expanded, parent, false)
        return MatchesStatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchesStatsViewHolder, position: Int) {
        holder.render(matchesStats)
    }

    override fun getItemCount() = 1
}