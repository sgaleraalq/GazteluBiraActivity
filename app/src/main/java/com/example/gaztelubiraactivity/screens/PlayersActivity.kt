package com.example.gaztelubiraactivity.screens

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.gaztelubiraactivity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File


class PlayersActivity : ComponentActivity() {

    private lateinit var tlPlayerStats: TableLayout

    private lateinit var btnSortName: Button
    private lateinit var btnSortGoals: Button
    private lateinit var btnSortAssists: Button
    private lateinit var btnSortGamesPlayed: Button
    private lateinit var btnSortProportionMatchesGoals: Button
    private lateinit var btnAddNewPlayer: Button

    private lateinit var jsonFile: String


//    DIALOG VARIABLES
    private lateinit var addPlayerDialog: Dialog
    private lateinit var etDialogName: EditText
    private lateinit var btnDialogAddPlayer: Button
    private lateinit var tvNumberOfGoals: TextView
    private lateinit var tvNumberOfAssists: TextView
    private lateinit var tvNumberOfGames: TextView
    private lateinit var fabMinusGoals: FloatingActionButton
    private lateinit var fabPlusGoals: FloatingActionButton
    private lateinit var fabMinusAssists: FloatingActionButton
    private lateinit var fabPlusAssists: FloatingActionButton
    private lateinit var fabMinusGames: FloatingActionButton
    private lateinit var fabPlusGames: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.players_activity)

        initComponents()
        initListeners()
        initUI()
    }

    private fun initUI() {
        parseJson()
    }

    private fun initComponents() {
        btnSortName = findViewById(R.id.btnPlayerName)
        btnSortGoals = findViewById(R.id.btnPlayerGoals)
        btnSortAssists = findViewById(R.id.btnPlayerAssists)
        btnSortGamesPlayed = findViewById(R.id.btnPlayerMatches)
        btnSortProportionMatchesGoals = findViewById(R.id.btnPlayerProportionMatchesGoals)
        btnAddNewPlayer = findViewById(R.id.btnAddNewPlayer)
        tlPlayerStats = findViewById(R.id.tlPlayersStats)
    }

    private fun initListeners() {
        btnSortName.setOnClickListener { sortElements("name") }
        btnSortGoals.setOnClickListener { sortElements("goals") }
        btnSortAssists.setOnClickListener { sortElements("assists") }
        btnSortGamesPlayed.setOnClickListener { sortElements("gamesPlayed") }
        btnSortProportionMatchesGoals.setOnClickListener { sortElements("proportionMatchesGoals") }
        btnAddNewPlayer.setOnClickListener { showAddNewPlayerDialog() }
    }

    private fun parseJson() {
        try {
            jsonFile = assets.open("players.json").bufferedReader().use { it.readText() }
            sortElements("name")
        } catch (ioException: IOException) {
            println("Error: $ioException")
        }
    }

    private fun sortElements(value: String) {
        val gson = Gson()
        var jsonObject = gson.fromJson(jsonFile, JsonObject::class.java)
        jsonObject = updateProportions(jsonObject)

        val playersArray =
            when (value) {
                "name" -> jsonObject.getAsJsonArray("players")
                    .sortedBy { it.asJsonObject.get(value).asString }

                "proportionMatchesGoals" -> jsonObject.getAsJsonArray("players")
                    .sortedByDescending { it.asJsonObject.get(value).asDouble }

                else -> jsonObject.getAsJsonArray("players")
                    .sortedByDescending { it.asJsonObject.get(value).asInt }
            }

        tlPlayerStats.removeViews(1, tlPlayerStats.childCount - 1)
        insertPlayerToTable(playersArray)
    }

    private fun updateProportions(jsonObj: JsonObject): JsonObject {
        if (jsonObj.has("players") && jsonObj.get("players").isJsonArray) {
            val playersArray = jsonObj.getAsJsonArray("players")
            for (playerElement in playersArray) {
                val goals = playerElement.asJsonObject.get("goals").asInt
                val gamesPlayed = playerElement.asJsonObject.get("gamesPlayed").asInt
                val proportionMatchesGoals =
                    if (gamesPlayed > 0) goals.toDouble() / gamesPlayed.toDouble() else 0.0
                playerElement.asJsonObject.addProperty(
                    "proportionMatchesGoals",
                    proportionMatchesGoals
                )
            }
        }
        return jsonObj
    }

    private fun insertPlayerToTable(array: List<JsonElement>) {
        for (playerElement in array) {
            val player = Gson().fromJson(playerElement, Player::class.java)

            val view: View = layoutInflater.inflate(R.layout.row_holder_view, tlPlayerStats, false)
            val rowViewHolder = RowViewHolder(view, player)
            rowViewHolder.render(player)

            tlPlayerStats.addView(view)
        }
    }

//    DIALOG FUNCTIONS
    private fun showAddNewPlayerDialog() {
        addPlayerDialog = Dialog(this)
        addPlayerDialog.setContentView(R.layout.dialog_new_player)
        addPlayerDialog.show()

        etDialogName = addPlayerDialog.findViewById(R.id.etNewPlayerName)
        tvNumberOfGoals = addPlayerDialog.findViewById(R.id.tvDialogGoals)
        tvNumberOfAssists = addPlayerDialog.findViewById(R.id.tvDialogAssists)
        tvNumberOfGames = addPlayerDialog.findViewById(R.id.tvDialogGames)

//        INIT ALL VARIABLES FROM DIALOG
        initDialogComponents(addPlayerDialog)
        initDialogListeners()
    }

    private fun initDialogComponents(addPlayerDialog:Dialog){
        fabMinusGoals = addPlayerDialog.findViewById(R.id.fabMinusGoals)
        fabPlusGoals = addPlayerDialog.findViewById(R.id.fabPlusGoals)

        fabMinusAssists = addPlayerDialog.findViewById(R.id.fabMinusAssists)
        fabPlusAssists = addPlayerDialog.findViewById(R.id.fabPlusAssists)

        fabMinusGames = addPlayerDialog.findViewById(R.id.fabMinusGames)
        fabPlusGames = addPlayerDialog.findViewById(R.id.fabPlusGames)

        btnDialogAddPlayer = addPlayerDialog.findViewById(R.id.btnDialogAddPlayer)
    }

    private fun initDialogListeners(){
        fabMinusGoals.setOnClickListener { subtractDialog(tvNumberOfGoals) }
        fabPlusGoals.setOnClickListener { addDialog(tvNumberOfGoals) }

        fabMinusAssists.setOnClickListener { subtractDialog(tvNumberOfAssists) }
        fabPlusAssists.setOnClickListener { addDialog(tvNumberOfAssists) }

        fabMinusGames.setOnClickListener { subtractDialog(tvNumberOfGames) }
        fabPlusGames.setOnClickListener { addDialog(tvNumberOfGames) }

        btnDialogAddPlayer.setOnClickListener { addPlayer() }
    }

    private fun subtractDialog(textView: TextView) {
        val goals = textView.text.toString().toInt()
        if (goals > 0) textView.text = (goals - 1).toString()
    }

    private fun addDialog(textView: TextView) {
        textView.text = (textView.text.toString().toInt() + 1).toString()
    }

    private fun addPlayer() {
        if (etDialogName.text.toString().isEmpty()){
            warningEmptyName()
            return
        }
        if ((tvNumberOfGoals.text.toString().toInt()>0 || tvNumberOfAssists.text.toString().toInt()>0) &&
            tvNumberOfGames.text.toString().toInt()==0) {
            warningIncorrectParameters(etDialogName.context)
            return
        }
        else {
            insertNewPlayer()
            addPlayerDialog.dismiss()
        }
    }

    private fun warningEmptyName(){
        etDialogName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.fabRed))
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
        etDialogName.startAnimation(shakeAnimation)
        etDialogName.error=etDialogName.context.getString(R.string.warningEmptyName)
    }

    private fun warningIncorrectParameters(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.warning))
        builder.setMessage(context.getString(R.string.warningIncorrectParameters))
        builder.setPositiveButton(context.getString(R.string.accept)) { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun insertNewPlayer() {
        var existingJson: JsonObject = Gson().fromJson(jsonFile, JsonObject::class.java)
        val newPlayer = JsonObject()
        newPlayer.addProperty("name", etDialogName.text.toString())
        newPlayer.addProperty("goals", tvNumberOfGoals.text.toString().toInt())
        newPlayer.addProperty("assists", tvNumberOfAssists.text.toString().toInt())
        newPlayer.addProperty("gamesPlayed", tvNumberOfGames.text.toString().toInt())

        val playersArray = existingJson.getAsJsonArray("players")
        playersArray.add(newPlayer)

        existingJson.add("players", playersArray)
        val updatedJsonString = Gson().toJson(existingJson)
        try {
            File("players.json").writeText(updatedJsonString)
        } catch (e: Exception) { e.printStackTrace() }
    }

}