package com.example.gaztelubiraactivity

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestResult

object SupabaseManager {
    lateinit var client: SupabaseClient
    lateinit var players: PostgrestResult
    lateinit var games: PostgrestResult
    lateinit var userAuth: PostgrestResult
    lateinit var mvpStats: PostgrestResult

    fun initialize() {
        client = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(GoTrue)
            install(Postgrest)
        }
    }

    suspend fun getData() {
        players = client.postgrest.from("players").select()
        games = client.postgrest.from("games").select()
        userAuth = client.postgrest.from("user_auth").select()
        mvpStats = client.postgrest.from("MVP").select()
    }
}
