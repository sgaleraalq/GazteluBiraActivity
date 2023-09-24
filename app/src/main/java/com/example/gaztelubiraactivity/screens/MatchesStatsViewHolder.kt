package com.example.gaztelubiraactivity.screens

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R
import com.example.gaztelubiraactivity.SupabaseManager
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import kotlin.reflect.full.memberProperties

class MatchesStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val cvMatchesStatsGoals: CardView = view.findViewById(R.id.cvMatchesStatsGoals)
    private val cvMatchesStatsAssits: CardView = view.findViewById(R.id.cvMatchesStatsAssits)
    private val cvMatchesStatsPlayers: CardView = view.findViewById(R.id.cvMatchesStatsPlayers)
    private val cvMatchesStatsMVP: CardView = view.findViewById(R.id.cvMatchesStatsMVP)
    private val dialog: Dialog = Dialog(itemView.context)


    fun render(matchesStats: MatchesStats, userName: String, rival: String) {
        initListeners(matchesStats, userName, rival)
    }

    @SuppressLint("SimpleDateFormat")
    fun isSevenDaysAgo(dateString: String, formatPattern: String): Boolean {
        try {
            val sdf = SimpleDateFormat(formatPattern)
            val date = sdf.parse(dateString)

            if (date != null) {
                val calendar =
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")) // Utiliza la zona horaria adecuada
                val today = calendar.time

                // Calcula la diferencia en milisegundos
                val differenceInMillis = today.time - date.time

                // Convierte los milisegundos a días
                val daysDifference = differenceInMillis / (1000 * 60 * 60 * 24)

                return daysDifference >= 7
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun initListeners(stats: MatchesStats, userName: String, rival: String) {
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
            val mvpStats =
                Json.decodeFromString<List<MVP>>(SupabaseManager.mvpStats.body.toString())
                    .filter { it.match == rival }

            val eligiblePlayers = getEligiblePlayers(mvpStats[0])

            if (isEligible(userName, eligiblePlayers)) {
                if (closedVotation(mvpStats[0].createdAt)) {
                    println("Past 7 days")
                    showFinalStats()
                } else {
                    println("Not past 7 days")
                    showMVPDialog(stats, userName)
                }
            } else {
                println("Not eligible")
                if (closedVotation(mvpStats[0].createdAt)) {
                    showStats()
                } else {
                    showFinalStats()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showNonMVPDialog(context: String, stats: MatchesStats) {
        dialog.setContentView(R.layout.dialog_stats)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_border)
        val dialogTitle = dialog.findViewById<TextView>(R.id.tvDialogStats)
        val dialogImage = dialog.findViewById<ImageView>(R.id.ivDialogStats)
        val dialogList = dialog.findViewById<ListView>(R.id.lvDialogStats)


        when (context) {
            "Goals" -> {
                dialogTitle.text = "Goals"
                dialogImage.setImageResource(R.drawable.football_ball)
                dialogList.adapter =
                    ArrayAdapter(itemView.context, R.layout.players_in_list, stats.goals.toList())
            }

            "Assists" -> {
                dialogTitle.text = "Assists"
                dialogImage.setImageResource(R.drawable.football_shoe)
                dialogList.adapter =
                    ArrayAdapter(itemView.context, R.layout.players_in_list, stats.assits.toList())
            }

            else -> {
                dialogTitle.text = "Players"
                dialogImage.setImageResource(R.drawable.group_players)
                dialogList.adapter = ArrayAdapter(
                    itemView.context,
                    R.layout.players_in_list,
                    stats.players.sortedBy { it }.toList()
                )
            }
        }
        dialog.show()
    }

    private fun getEligiblePlayers(mvpStats: MVP): MutableList<String> {
        val eligiblePlayers = mutableListOf<String>()
        for (prop in mvpStats::class.memberProperties) {
            if (prop.getter.call(mvpStats) == null) {
                eligiblePlayers.add(prop.name)
            }
        }
        return eligiblePlayers
    }

    private fun isEligible(user: String, eligiblePlayers: MutableList<String>): Boolean {
        if (user.lowercase() == "Guest") return false
        else if (user.lowercase() !in eligiblePlayers) return false
        return true
    }

    private fun closedVotation(date: String): Boolean {
        val formatPattern = "yyyy-MM-dd"

        return isSevenDaysAgo(date, formatPattern)
    }

    private fun showMVPDialog(stats: MatchesStats, userName: String) {
        dialog.setContentView(R.layout.mvp_dialog)
        val rgMvpPlayers = dialog.findViewById<RadioGroup>(R.id.rgMVP)
        val btnSendMVP: Button = dialog.findViewById(R.id.btnSendMVP)

        for (player in stats.players) {
            if (player == userName) continue

            val rbPlayer = RadioButton(itemView.context)

            // Configuración del formato del RadioButton
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 8, 0, 8)
            rbPlayer.layoutParams = layoutParams
            rbPlayer.setBackgroundResource(R.drawable.radio_button_mvp_border)
            rbPlayer.setPadding(16, 16, 16, 16)
            rbPlayer.textSize = 20f
            rbPlayer.setTypeface(null, Typeface.BOLD)
            rbPlayer.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.black))
            rbPlayer.text = player

            // Agregar el RadioButton al RadioGroup o al contenedor deseado (en este caso, llMvpPlayers)
            rgMvpPlayers.addView(rbPlayer)
        }

        dialog.show()
        btnSendMVP.setOnClickListener { showStats(true) }
    }

    private fun showStats(fromDialog: Boolean = false) {
//        Remove scroll view from dialog
        if (!fromDialog){
            dialog.setContentView(R.layout.mvp_dialog)
            dialog.show()
        }

        val svMVP: ScrollView = dialog.findViewById(R.id.svMVP)
        val btnSendMVP: Button = dialog.findViewById(R.id.btnSendMVP)
        btnSendMVP.visibility = View.GONE
        svMVP.visibility = View.GONE

//        TODO Add MVP votes

    }

    private fun showFinalStats() {

    }
}