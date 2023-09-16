package com.example.gaztelubiraactivity.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesAdapter(private var matches: List<Matches>) :
    RecyclerView.Adapter<MatchesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_expandable, parent, false)
        return MatchesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int){
        holder.render(matches[position], holder.itemView)

        val holderMatchesStats = holder.MatchesRecyclerViewHolder(holder.itemView)
        holderMatchesStats.matchesStatsRecyclerView.setHasFixedSize(true)
        holderMatchesStats.matchesStatsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        val toAdapt = MatchesStats(matches[position].scorers, matches[position].assistants, matches[position].players)
        val adapter = MatchesStatsAdapter(toAdapt)
        holderMatchesStats.matchesStatsRecyclerView.adapter = adapter

//        Expandable Functionality
        val isExpandable = matches[position].isExpandable
        holderMatchesStats.matchesStatsRecyclerView.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holderMatchesStats.cardView.setOnClickListener {
            isAnyItemExpanded(position)
            matches[position].isExpandable = !matches[position].isExpandable
            notifyItemChanged(position)
        }

    }
    override fun getItemCount() = matches.size
    private fun isAnyItemExpanded(position: Int){
        val temp = matches.indexOfFirst { it.isExpandable }
        if (temp >= 0 && temp != position){
            matches[temp].isExpandable = false
            notifyItemChanged(temp)
        }
    }

    fun clearData() {
        matches = emptyList() // Vaciar la lista de datos
        notifyDataSetChanged() // Notificar al RecyclerView que los datos han cambiado
    }
}