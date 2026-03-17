package database
import java.sql.Connection
import java.sql.DriverManager
object DatabaseManager {
    // Ruta relativa on hi ha el fitxer de la base de dades
    private const val DATABASE_FILE = "./src/main/kotlin/database/netflix.db"

    // La connexió es crea només un cop, quan es crida per primer cop
    public val connection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite:$DATABASE_FILE")
    }

}