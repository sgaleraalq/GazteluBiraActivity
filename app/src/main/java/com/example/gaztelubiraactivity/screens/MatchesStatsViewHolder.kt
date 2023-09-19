package com.example.gaztelubiraactivity.screens

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
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
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R

class MatchesStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val cvMatchesStatsGoals: CardView = view.findViewById(R.id.cvMatchesStatsGoals)
    private val cvMatchesStatsAssits: CardView = view.findViewById(R.id.cvMatchesStatsAssits)
    private val cvMatchesStatsPlayers: CardView = view.findViewById(R.id.cvMatchesStatsPlayers)
    private val cvMatchesStatsMVP: CardView = view.findViewById(R.id.cvMatchesStatsMVP)
    private val dialog: Dialog = Dialog(itemView.context)


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
            showMVPDialog(stats)
        }
    }

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

    private fun showMVPDialog(stats: MatchesStats) {
        dialog.setContentView(R.layout.mvp_dialog)
        val rgMvpPlayers = dialog.findViewById<RadioGroup>(R.id.rgMVP)
        val btnSendMVP: Button = dialog.findViewById(R.id.btnSendMVP)

        for (player in stats.players) {
            val rbPlayer = RadioButton(itemView.context)

//            This only is for creating the radio button style
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            val paddingInDp = 10
            val paddingInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                paddingInDp.toFloat(),
                itemView.resources.displayMetrics
            ).toInt()
            rbPlayer.layoutParams = layoutParams
            rbPlayer.setBackgroundResource(R.drawable.radio_button_mvp_border)
            rbPlayer.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            layoutParams.setMargins(5, 5, 5, 5)
            rbPlayer.textSize = 20f
//            Here it finishes

            rbPlayer.text = player
            rgMvpPlayers.addView(rbPlayer)
        }

        dialog.show()
        btnSendMVP.setOnClickListener { showStats() }
    }

    private fun showStats(){
//        Remove scroll view from dialog
        val svMVP: ScrollView = dialog.findViewById(R.id.svMVP)
        svMVP.visibility = View.GONE
//        TODO Add MVP votes

    }
}