package com.example.gaztelubiraactivity.screens

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.gaztelubiraactivity.R
import com.example.gaztelubiraactivity.SupabaseManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.runBlocking
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

            if (closedVotation(mvpStats[0].createdAt)) {
                showFinalStats(mvpStats[0])
            } else {
                if (isEligible(userName, eligiblePlayers)) {
                    showMVPDialog(stats, userName, rival, mvpStats[0])
                } else {
                    showStats(mvpStats[0])
                }
            }
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
                dialogTitle.text = getString(itemView.context, R.string.tvNumberOfGoals)
                dialogImage.setImageResource(R.drawable.football_ball)
                dialogList.adapter =
                    ArrayAdapter(itemView.context, R.layout.players_in_list, stats.goals.toList())
            }

            "Assists" -> {
                dialogTitle.text = getString(itemView.context, R.string.tvNumberOfAssists)
                dialogImage.setImageResource(R.drawable.football_shoe)
                dialogList.adapter =
                    ArrayAdapter(itemView.context, R.layout.players_in_list, stats.assits.toList())
            }

            else -> {
                dialogTitle.text = getString(itemView.context, R.string.tvNumberOfPlayers)
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

    private fun showMVPDialog(stats: MatchesStats, userName: String, rival: String, mvpStats: MVP) {
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
        btnSendMVP.setOnClickListener {
            val rbSelectedUser =
                rgMvpPlayers.findViewById<RadioButton>(rgMvpPlayers.checkedRadioButtonId)
            val votedPlayer = rbSelectedUser.text.toString()

            validateVote(userName.lowercase(), votedPlayer, rival)
            dialog.dismiss()
            showStats(mvpStats, votedPlayer)
        }
    }

    private fun validateVote(userName: String, votePlayer: String, rival: String) {
        try {
            runBlocking {
                SupabaseManager.client.postgrest["MVP"].update(
                    {
                        set(userName, votePlayer)
                    })
                {
                    eq("match", rival)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showAlert(R.string.mvpVoteFail, R.string.mvpVoteFailDescription)
        }
    }

    private fun showStats(mvpStats: MVP, votedPlayer: String? = null) {
        val statsMVPDialog = Dialog(itemView.context)
        statsMVPDialog.setContentView(R.layout.bar_chart_mvp)

        showMostVotedPlayers(mvpStats, statsMVPDialog, votedPlayer)

        statsMVPDialog.show()
    }

    private fun showMostVotedPlayers(
        mvpStats: MVP,
        statsMVPDialog: Dialog,
        votedPlayer: String? = null
    ) {
        val mostVotedPlayers = mutableListOf<String>()

        if (votedPlayer != null) {
            mostVotedPlayers.add(votedPlayer)
        }

        for (prop in mvpStats::class.memberProperties) {
            if (prop.name == "match" || prop.name == "createdAt" || prop.name == "id") continue
            if (prop.getter.call(mvpStats) == null || prop.getter.call(mvpStats) == "None") continue
            else {
                mostVotedPlayers.add(prop.getter.call(mvpStats).toString())
            }
        }

        val countedPlayers = mostVotedPlayers.groupingBy { it }.eachCount().entries
            .sortedByDescending { it.value }
            .filter { it.value > 0 }
            .sortedBy { it.value }


        val entries = ArrayList<BarEntry>()
        var index = 0f

        for ((_, votes) in countedPlayers) {
            val barEntry = BarEntry(index, votes.toFloat())
            entries.add(barEntry)
            index += 1f
        }

        addBarChartToDialog(statsMVPDialog, entries, countedPlayers)
    }


    @SuppressLint("SetTextI18n")
    private fun showFinalStats(mvpStats: MVP) {
        dialog.setContentView(R.layout.final_mvp_stats_dialog)
        val tvFinalMVPWinner: TextView = dialog.findViewById(R.id.finalMVPWinner)

        val mostVotedPlayers = mutableListOf<String>()
        for (prop in mvpStats::class.memberProperties) {
            if (prop.name == "match" || prop.name == "createdAt" || prop.name == "id") continue
            if (prop.getter.call(mvpStats) == null || prop.getter.call(mvpStats) == "None") continue
            else {
                mostVotedPlayers.add(prop.getter.call(mvpStats).toString())
            }
        }

        if (mostVotedPlayers.size == 0) {
            tvFinalMVPWinner.text = "No MVP"
            dialog.show()
            return
        }
        val countedPlayers = mostVotedPlayers.groupingBy { it }.eachCount().entries
            .sortedByDescending { it.value }
            .take(1)
        tvFinalMVPWinner.text = countedPlayers[0].key
    }

    private fun addBarChartToDialog(
        chartView: Dialog,
        mostVotedPlayers: List<BarEntry>,
        countedPlayers: List<Map.Entry<String, Int>>
    ) {
        val barChart: BarChart = chartView.findViewById(R.id.barChartMVP)

        // Configurar los datos del gráfico
        val barDataSet = BarDataSet(mostVotedPlayers, "MVP Votes")
        val barData = BarData(barDataSet)

        // Personalizar colores, etiquetas, etc. aquí si es necesario

        // Configurar el gráfico de barras
        barChart.data = barData
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f

        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        // Configurar el fondo del gráfico como transparente
        barChart.setBackgroundColor(Color.TRANSPARENT)

        // Deshabilitar las líneas de la cuadrícula vertical y horizontal detrás del gráfico
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)

        // Deshabilitar el eje X (horizontal) y el eje Y (vertical)
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.labelCount = 1

        // Configurar etiquetas en el eje X (horizontal)
        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Aquí puedes personalizar cómo se muestran las etiquetas en el eje X
                val index = value.toInt()
                if (index >= 0 && index < countedPlayers.size) {
                    return countedPlayers[index].key // Usar las strings de countedPlayers como etiquetas
                }
                return "" // Devolver una cadena vacía si el índice está fuera de rango
            }
        }

        // Establecer la posición de las etiquetas en el eje X (debajo de las barras)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity =
            1f // Establecer la granularidad en 1 para que las etiquetas vayan de 1 en 1

        // Mostrar el valor en la barra
        barDataSet.setDrawValues(true)

        // Deshabilitar la descripción (leyenda) del gráfico
        barChart.description.isEnabled = false

        // Deshabilitar la leyenda (etiquetas en el fondo)
        barChart.legend.isEnabled = false

        // Actualizar el gráfico
        barChart.invalidate()
    }


    private fun showAlert(errorMessage: Int, message: Int) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle(errorMessage)
        builder.setMessage(message)
        builder.setPositiveButton("Accept") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}