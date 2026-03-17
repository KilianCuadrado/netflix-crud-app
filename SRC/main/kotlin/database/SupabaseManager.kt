package database

import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

object SupabaseManager {
    private val URL = System.getProperty("SUPABASE_URL") ?: throw IllegalStateException("URL de Supabase no trobada")
    private val USER = System.getProperty("SUPABASE_USER")
    private val PASS = System.getProperty("SUPABASE_PASS")
    private val PORT = System.getProperty("SUPABASE_PORT")

    // La connexió es crea només un cop, quan es crida per primer cop
    public val connection: Connection by lazy {
        val props = Properties().apply {
            setProperty("user", USER)
            setProperty("password", PASS)
            setProperty("port", PORT)
            setProperty("sslmode", "require")
        }
        DriverManager.getConnection(URL, props)
    }
}
