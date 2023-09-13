package com.example.gaztelubiraactivity.screens

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvNameLocal: TextView = view.findViewById(R.id.tvMainItemExpandableNameLocal)
    private val tvNameVisitor: TextView = view.findViewById(R.id.tvMainItemExpandableNameVisitor)
    private val tvGoalsLocal: TextView = view.findViewById(R.id.tvMainItemExpandableGoalsLocal)
    private val tvGoalsVisitor: TextView = view.findViewById(R.id.tvMainItemExpandableGoalsVisitor)
    private val ivLocal: ImageView = view.findViewById(R.id.ivMainItemExpandableLogoLocal)
    private val ivVisitor: ImageView = view.findViewById(R.id.ivMainItemExpandableLogoVisitor)

    private val cvResult: CardView = view.findViewById(R.id.cvMainItemExpandable)

    fun render(match: Matches, view: View) {

        if (match.isSelected) {
            println("Expand")
        } else {
            println("Don't expand")
        }

        tvNameLocal.text = match.local
        tvNameVisitor.text = match.visitor
        tvGoalsLocal.text = match.localGoals.toString()
        tvGoalsVisitor.text = match.visitorGoals.toString()

        var result = getResult(match)

        var color = when (result) {
            'W' -> ContextCompat.getColor(view.context, R.color.tvGamesGreen)
            'D' -> ContextCompat.getColor(view.context, R.color.tvGamesYellow)
            else -> ContextCompat.getColor(view.context, R.color.tvGamesRed)
        }

//        Set element background color as color
        cvResult.setCardBackgroundColor(color)

        val localLogo = setLogos(tvNameLocal.text.toString())
        val visitorLogo = setLogos(tvNameVisitor.text.toString())
        ivLocal.setImageResource(localLogo)
        ivVisitor.setImageResource(visitorLogo)
    }


    private fun getResult(match: Matches): Char {
        if (match.localGoals == match.visitorGoals) {
            return 'D'
        }
        return if (tvNameLocal.text == "Gaztelu Bira") {
            getLocalResult(match)
        } else {
            getVisitorResult(match)
        }
    }

    private fun getLocalResult(match: Matches): Char {
        return if (match.localGoals > match.visitorGoals) {
            'W'
        } else {
            'L'
        }
    }

    private fun getVisitorResult(match: Matches): Char {
        return if (match.localGoals < match.visitorGoals) {
            'W'
        } else {
            'L'
        }
    }

    private fun setLogos(team: String): Int {
        return if (team == "Gaztelu Bira") { R.drawable.gaztelu_bira_logo } else {
            R.drawable.no_logo }
    }
}