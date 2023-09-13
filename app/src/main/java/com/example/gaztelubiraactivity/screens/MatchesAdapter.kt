package com.example.gaztelubiraactivity.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesAdapter(private var matches: List<Matches>) :
    RecyclerView.Adapter<MatchesViewHolder>() {
//
//        inner class MatchesRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//            val matchesStatsRecyclerView: RecyclerView = itemView.findViewById(R.id.rvMatchesStats)
//        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_expandable, parent, false)
        return MatchesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        holder.render(matches[position], holder.itemView)

//        holder.matchesStatsRecyclerView.setHasFixedSize(true)
//        holder.itemView.setOnClickListener{ onGameSelected(position) }
    }

    override fun getItemCount() = matches.size
}