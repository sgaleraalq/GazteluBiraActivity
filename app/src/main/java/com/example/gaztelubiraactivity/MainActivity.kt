package com.example.gaztelubiraactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.gaztelubiraactivity.screens.MatchesActivity
import com.example.gaztelubiraactivity.screens.PlayersActivity


class MainActivity : ComponentActivity() {

    private lateinit var btnPlayers: Button
    private lateinit var btnMatches: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        initComponents()
        initListeners()
    }

    private fun initUI(){
        println("POINTS TO BE SET")
    }

    private fun initComponents(){
        btnPlayers = findViewById(R.id.btnPlayers)
        btnMatches = findViewById(R.id.btnMatches)
    }

    private fun initListeners(){
        btnPlayers.setOnClickListener { playersIntent() }
        btnMatches.setOnClickListener { matchesIntent() }
    }

    private fun playersIntent(){
        val intent = Intent(this, PlayersActivity::class.java)
        startActivity(intent)
    }

    private fun matchesIntent(){
        val intent = Intent(this, MatchesActivity::class.java)
        startActivity(intent)
    }

}