package dao

import database.DatabaseManager
import model.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.SQLTimeoutException

/**
 * Objecte d'accés a dades (DAO) per gestionar els registres de contingut a la base de dades de Netflix.
 * Proporciona mètodes per consultar, inserir, actualitzar i eliminar registres de pel·lícules i sèries,
 * i s'encarrega de convertir les files SQL en objectes de domini.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see DatabaseManager
 */
public class NetflixDao: INetflixDao {
    private val TABLE_NAME = "netflix_titles_cleaned"
    private val ID_COLUMN = "show_id"
    private val databaseConnection: Connection

    constructor (databaseSource: Connection, ){
        this.databaseConnection = databaseSource
    }
    /**
     * Conjunt de columnes permeses per a ordenació dinàmica.
     * Es fa servir per evitar injeccions SQL quan l'usuari tria la columna.
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private val allowedOrderColumns = setOf(
        ID_COLUMN,
        "type",
        "title",
        "country",
        "date_added",
        "release_year",
        "rating",
        "duration",
        "duration_value"
    )

    /**
     * Escapa valors de text per construir literals SQL bàsics en consultes directes.
     */
    private fun sqlStringLiteral(value: String?): String {
        val safe = value?.replace("'", "''") ?: ""
        return "'$safe'"
    }

    /**
     * Converteix una fila de ResultSet en una instància de Media.
     * Detecta automàticament si la fila és una pel·lícula o una sèrie i construeix
     * l'objecte corresponent amb les conversions de rating, gèneres, direcció i càsting.
     * @param rs fila actual del ResultSet
     * @return objecte Media construït a partir de la fila
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun mapResultSetToMedia(rs: ResultSet): Media {
        val mediaRating = MediaRating.fromString(rs.getString("rating") ?: "")
        val listedInRaw = rs.getString("listed_in") ?: ""
        // Converteix el camp de gèneres en llista: separa per comes, neteja espais i descarta buits.
        val listedInList = listedInRaw
            .split(",")
            .mapNotNull { token ->
                val clean = token.trim()
                if (clean.isEmpty()) null else MediaGenere.fromString(clean)
            }
            .toMutableList()

        // Parseja directors des de text CSV-like i crea objectes només per noms vàlids.
        val directors = (rs.getString("director") ?: "")
            .split(",")
            .mapNotNull { token -> token.trim().takeIf { it.isNotEmpty() }?.let { Director(it) } }
            .toMutableList()

        // Mateix procés pel càsting: trim + filtre de buits + conversió a Actor.
        val cast = (rs.getString("cast") ?: "")
            .split(",")
            .mapNotNull { token -> token.trim().takeIf { it.isNotEmpty() }?.let { Actor(it) } }
            .toMutableList()

        val mediaTypeString = rs.getString("type") ?: ""

        return if (mediaTypeString == MediaType.MOVIE.value) {
            Pelicula(
                rs.getString(ID_COLUMN),
                rs.getString("title") ?: "",
                directors,
                cast,
                rs.getString("country") ?: "",
                rs.getString("date_added") ?: "",
                rs.getInt("release_year"),
                mediaRating,
                rs.getString("duration") ?: "",
                listedInList,
                rs.getString("description") ?: "",
                rs.getInt("duration_value")
            )
        } else {
            Series(
                rs.getString(ID_COLUMN),
                rs.getString("title") ?: "",
                directors,
                cast,
                rs.getString("country") ?: "",
                rs.getString("date_added") ?: "",
                rs.getInt("release_year"),
                mediaRating,
                rs.getString("duration") ?: "",
                listedInList,
                rs.getString("description") ?: "",
                rs.getInt("duration_value")
            )
        }
    }

    /**
     * Recupera tots els continguts de la base de dades i els retorna com una llista d'objectes `Media`.
     * Executa una consulta SQL sobre la taula i transforma cada fila del `ResultSet`
     * en `Pelicula` o `Series` segons el tipus.
     * @return llista amb tots els registres de contingut.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Series
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució de la consulta.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun getAllMedia(): MutableList<Media> {
        val media = mutableListOf<Media>()
        val sql = "SELECT * FROM $TABLE_NAME"

        databaseConnection.createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                while (rs.next()) {
                    media.add(mapResultSetToMedia(rs))
                }
            }
        }

        return media
    }

    /**
     * Recupera tots els continguts ordenats per la columna indicada i pel sentit d'ordenació.
     * Valida la columna contra una llista permesa per evitar injecció SQL i després
     * construeix la consulta amb `ORDER BY`.
     * @param column nom de la columna per ordenar. Ha d'estar dins de les columnes permeses: ${allowedOrderColumns.joinToString(", ")}.
     * @param ascending sentit d'ordenació (`true` ascendent, `false` descendent). Per defecte és `true`.
     * @return llista de continguts ordenada segons els paràmetres indicats.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Series
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució de la consulta.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun getAllMediaOrdered(column: String, ascending: Boolean): MutableList<Media> {
        val safeColumn = if (allowedOrderColumns.contains(column)) column else "title"
        val direction = if (ascending) "ASC" else "DESC"
        val media = mutableListOf<Media>()
        val sql = "SELECT * FROM $TABLE_NAME ORDER BY $safeColumn $direction"

        databaseConnection.createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                while (rs.next()) {
                    media.add(mapResultSetToMedia(rs))
                }
            }
        }

        return media
    }

    /**
     * Recupera un registre de contingut pel seu identificador.
     * Si existeix, retorna l'objecte `Media` corresponent; si no existeix, retorna `null`.
     * @param id identificador del contingut a cercar.
     * @return objecte `Media` trobat o `null` si no existeix.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Media
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució de la consulta.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun getMediaById(id: String): Media? {
        val sql = "SELECT * FROM $TABLE_NAME WHERE $ID_COLUMN = ${sqlStringLiteral(id)}"

        databaseConnection.createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                if (rs.next()) {
                    return mapResultSetToMedia(rs)
                }
            }
        }

        return null
    }

    /**
     * Recupera totes les sèries de televisió de la base de dades.
     * Internament filtra la llista global per instàncies de `Series`.
     * @return llista amb totes les sèries.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Series
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun getAllSeries(): List<Series> {

        return getAllMedia().filterIsInstance<Series>() // Filtra la llista de media per obtenir només les sèries
    }

    /**
     * Recupera totes les pel·lícules de la base de dades.
     * Internament filtra la llista global per instàncies de `Pelicula`.
     * @return llista amb totes les pel·lícules.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Pelicula
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun getAllMovies(): List<Pelicula> {

        return getAllMedia().filterIsInstance<Pelicula>() // Filtra la llista de media per obtenir només les pel·lícules
    }

    /**
     * Elimina tots els registres de contingut de la base de dades.
     * Executa un `DELETE` sense condicions sobre la taula principal.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun deleteAllMedia() {
        val sql = "DELETE FROM $TABLE_NAME"

        databaseConnection.createStatement().use { stmt ->
            stmt.executeUpdate(sql)
        }
    }

    /**
     * Insereix un nou registre de contingut a la base de dades.
     * Construeix la sentència `INSERT` i assigna els valors amb `PreparedStatement`.
     * @param media objecte `Media` amb les propietats a inserir.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Media
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun insertMedia(media: Media) {
        val sql = """INSERT INTO $TABLE_NAME (
                |$ID_COLUMN,
                |title,
                |director,
                |"cast",
                |country,
                |date_added,
                |release_year,
                |rating,
                |duration,
                |listed_in,
                |description,
                |duration_value,
                |type
    |       ) VALUES (
                |${sqlStringLiteral(media.getId())},
                |${sqlStringLiteral(media.getTitle())},
                |${sqlStringLiteral(media.getDirector().joinToString(",") { it.getName() })},
                |${sqlStringLiteral(media.getCast().joinToString(",") { it.getName() })},
                |${sqlStringLiteral(media.getCountry())},
                |${sqlStringLiteral(media.getDateAdded())},
                |${media.getReleaseYear()},
                |${sqlStringLiteral(media.getRating().value)},
                |${sqlStringLiteral(media.getDuration())},
                |${sqlStringLiteral(media.getListedIn().joinToString(",") { it.value })},
                |${sqlStringLiteral(media.getDescription())},
                |${media.getDurationValue()},
                |${sqlStringLiteral(media.getType().value)}
    |       )""".trimMargin()

        databaseConnection.createStatement().use { stmt ->
            stmt.executeUpdate(sql)
        }
    }

    /**
     * Actualitza un registre de contingut existent de la base de dades.
     * Construeix una sentència `UPDATE` i localitza la fila a modificar pel seu ID.
     * @param media objecte `Media` amb les noves dades a persistir.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Media
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)
    public override fun updateMedia(media: Media): Boolean {
        val sql = """UPDATE $TABLE_NAME SET
                |title = ${sqlStringLiteral(media.getTitle())},
                |director = ${sqlStringLiteral(media.getDirector().joinToString(",") { it.getName() })},
                |"cast" = ${sqlStringLiteral(media.getCast().joinToString(",") { it.getName() })},
                |country = ${sqlStringLiteral(media.getCountry())},
                |date_added = ${sqlStringLiteral(media.getDateAdded())},
                |release_year = ${media.getReleaseYear()},
                |rating = ${sqlStringLiteral(media.getRating().value)},
                |duration = ${sqlStringLiteral(media.getDuration())},
                |listed_in = ${sqlStringLiteral(media.getListedIn().joinToString(",") { it.value })},
                |description = ${sqlStringLiteral(media.getDescription())},
                |duration_value = ${media.getDurationValue()},
                |type = ${sqlStringLiteral(media.getType().value)}
                |WHERE $ID_COLUMN = ${sqlStringLiteral(media.getId())}""".trimMargin()

        databaseConnection.createStatement().use { stmt ->
            return stmt.executeUpdate(sql) > 0
        }
    }

    /**
     * Elimina un registre concret de contingut de la base de dades.
     * Construeix una sentència `DELETE` per ID amb `PreparedStatement`.
     * @param media objecte `Media` que identifica el registre a eliminar.
     * @author KilianCuadrado
     * @since 2026-03-11
     * @version 1.0
     * @see Media
     * @Throws(SQLException::class, SQLTimeoutException::class)
     * SQLException – si hi ha un error d'accés a dades o la sentència SQL no és vàlida.
     * SQLTimeoutException – si s'excedeix el temps màxim d'execució.
     */
    @Throws(SQLException::class, SQLTimeoutException::class)

    public override fun deleteMedia(media: Media) {
        val sql = "DELETE FROM $TABLE_NAME WHERE $ID_COLUMN = ${sqlStringLiteral(media.getId())}"

        databaseConnection.createStatement().use { stmt ->
            stmt.executeUpdate(sql)
        }
    }
}