package controller

import dao.INetflixDao
import model.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

public class NetflixController {

    private val CSV_HEADER = "show_id,type,title,director,cast,country,date_added,release_year,rating,duration,listed_in,description,duration_value"

    private val netflixDao: INetflixDao

    constructor(netflixDao: INetflixDao){
        this.netflixDao = netflixDao
    }


    /**
     * Dona format de text a una llista de media per mostrar-la a la vista.
     * Si la llista és buida, retorna un missatge de no resultats.
     * @param mediaList llista de continguts a representar
     * @return text amb totes les entrades separades per línies en blanc
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun formatMediaList(mediaList: List<Media>): String {
        if (mediaList.isEmpty()) return "No s'han trobat dades."
        return mediaList.joinToString("\n\n") { it.toString() }
    }

    /**
     * Filtra les dades per tipus (pel·lícula, sèrie o tot) per reutilitzar la lògica d'exportació.
     * @param mediaType tipus opcional de contingut
     * @return llista de media segons el filtre indicat
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun getMediaByType(mediaType: MediaType?): List<Media> {
        return when (mediaType) {
            MediaType.MOVIE -> netflixDao.getAllMovies()
            MediaType.TV_SHOW -> netflixDao.getAllSeries()
            null -> netflixDao.getAllMedia()
        }
    }

    /**
     * Crea un fitxer de sortida amb nom únic dins la carpeta `files`.
     * Si el nom base ja existeix, afegeix sufix incremental `(1)`, `(2)`, etc.
     * @param baseName nom base introduït per l'usuari
     * @param extension extensió final del fitxer
     * @return instància de fitxer lliure de col·lisions
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun createUniqueFile(baseName: String, extension: String): File {
        val safeName = baseName.trim().ifEmpty { "export" }
        val dir = File("./files")
        dir.mkdirs()

        var candidate = File(dir, "$safeName.$extension")
        var index = 1

        while (candidate.exists()) {
            candidate = File(dir, "$safeName ($index).$extension")
            index++
        }

        return candidate
    }

    /**
     * Funció que retorna un string amb totes les dades de les pel·lícules i sèries de Netflix, separades per línies.
     * @return un string amb totes les dades de les pel·lícules i sèries de Netflix, separades per línies.
     * @author KilianCuadrado
     * @version 2.0
     * @since 2026-03-11
     * @see NetflixDao.getAllMedia
     */

    fun totesDadesMedia(): String {
        return formatMediaList(netflixDao.getAllMedia())
    }

    /**
     * Funció que exporta les dades de les pel·lícules i sèries de Netflix a un fitxer csv, txt o bin.
     * @author KilianCuadrado
     * @version 4.0
     * @since 2026-03-11
     * @param nomFitxer el nom del fitxer a exportar, sense extensió
     * @param extensio l'extensió del fitxer a exportar, pot ser "csv", "txt" o "bin"
     * @param mediaType el tipus de media a exportar, pot ser "Movie" o "TV Show"
     * @see NetflixDao.getAllMovies
     * @see model.Pelicula.toCSV
     */

    fun exportarDadesAFitxer(nomFitxer: String, extensio: String, mediaType: MediaType?): String {
        return try {
            val ext = extensio.lowercase().trim()
            val fitxer = createUniqueFile(nomFitxer, ext)

            when (ext) {
                "csv" -> exportarTotesDadesEnCSV(mediaType, fitxer)
                "txt" -> exportarTotesDadesEnTXT(mediaType, fitxer)
                "bin" -> exportarTotesDadesEnObjectes(mediaType, fitxer)
                else -> return "Extensio no valida. Usa csv, txt o bin."
            }

            "Exportacio completada a: ${fitxer.absolutePath}"
        } catch (e: Exception) {
            "Error en l'exportacio: ${e.message}"
        }
    }

    /**
     * Funció que exporta les dades de les pel·lícules i sèries de Netflix a estil csv en cas de no indicar ningun tipus de media s'exportaran totes.
     * @author KilianCuadrado
     * @version 2.0
     * @since 2026-03-11
     * @param mediaType el tipus de media a exportar, pot ser "Movie", "TV Show" o NULL per exportar totes les dades. Per defecte es null
     * @param fitxer el fitxer a exportar, amb la ruta completa i l'extensió
     * @see NetflixDao.getAllMovies
     * @see NetflixDao.getAllSeries
     */

    fun exportarTotesDadesEnCSV(mediaType: MediaType? = null, fitxer: File) {
        BufferedWriter(FileWriter(fitxer)).use { bw ->
            bw.write(CSV_HEADER)
            bw.newLine()
            getMediaByType(mediaType).forEach { media ->
                bw.write(media.toCSV())
                bw.newLine()
            }
        }
    }

    /**
     * Funció que exporta les dades de les pel·lícules i sèries de Netflix a un fitxer bin per guardar l'informacio en forma de objecte.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @param mediaType el tipus de media a exportar, pot ser "Movie", "TV Show" o NULL per exportar totes les dades. Per defecte es null
     * @param fitxer el fitxer a exportar, amb la ruta completa i l'extensió
     * @see NetflixDao.getAllMedia
     * @see Pelicula
     * @see Series
     */

    fun exportarTotesDadesEnObjectes(mediaType: MediaType? = null, fitxer: File) {
        ObjectOutputStream(FileOutputStream(fitxer)).use { oos ->
            getMediaByType(mediaType).forEach { media ->
                oos.writeObject(media)
            }
        }
    }

    /**
     * Funció que exporta les dades de les pel·lícules i sèries de Netflix a un fitxer txt per guardar l'informacio en forma de text. Separant cada media per una linia.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @param mediaType el tipus de media a exportar, pot ser "Movie", "TV Show" o NULL per exportar totes les dades. Per defecte es null
     * @param fitxer el fitxer a exportar, amb la ruta completa i l'extensió
     * @see NetflixDao.getAllMedia
     * @see Pelicula
     * @see Series
     */

    fun exportarTotesDadesEnTXT(mediaType: MediaType? = null, fitxer: File) {
        // TXT exportat en format parsejable (mateix esquema que CSV)
        BufferedWriter(FileWriter(fitxer)).use { bw ->
            bw.write(CSV_HEADER)
            bw.newLine()
            getMediaByType(mediaType).forEach { media ->
                bw.write(media.toCSV())
                bw.newLine()
            }
        }
    }

    /**
     * Funció que importa dades des d'un fitxer de text, afegint noves files a la base de dades de Netflix.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @param path la ruta del fitxer a importar
     * @see NetflixDao.insertMedia
     */

    fun importarDadesDesDeText(path: String): String {
        val file = File(path)
        if (!file.exists()) return "El fitxer no existeix."

        var imported = 0
        var skipped = 0

        BufferedReader(FileReader(file)).use { br ->
            br.lineSequence().forEachIndexed { index, line ->
                val clean = line.trim()
                if (clean.isEmpty()) return@forEachIndexed
                if (index == 0 && clean.startsWith("show_id")) return@forEachIndexed

                val media = parseMediaFromCsvLine(clean)
                if (media == null) {
                    skipped++
                } else if (netflixDao.getMediaById(media.getId()) == null) {
                    netflixDao.insertMedia(media)
                    imported++
                } else {
                    skipped++
                }
            }
        }

        return "Importacio finalitzada. Inserits: $imported, ignorats: $skipped"
    }

    /**
     * Funció que importa dades des d'un fitxer binari, afegint noves files a la base de dades de Netflix.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @param path la ruta del fitxer a importar
     * @see NetflixDao.insertMedia
     */

    fun importarDadesDesDeBin(path: String): String {
        val file = File(path)
        if (!file.exists()) return "El fitxer no existeix."

        var imported = 0
        var skipped = 0

        ObjectInputStream(FileInputStream(file)).use { ois ->
            try {
                while (true) {
                    val read = ois.readObject()
                    if (read is Media) {
                        if (netflixDao.getMediaById(read.getId()) == null) {
                            netflixDao.insertMedia(read)
                            imported++
                        } else {
                            skipped++
                        }
                    } else {
                        skipped++
                    }
                }
            } catch (_: EOFException) {
                // Final natural del fitxer.
            }
        }

        return "Importacio BIN finalitzada. Inserits: $imported, ignorats: $skipped"
    }

    /**
     * Funció que afegeix una nova fila a la base de dades de Netflix, si el ID no existeix ja en la base de dades.
     * @autor KilianCuadrado
     * @version 1.0
     * @since 2026-03-11
     * @see NetflixDao.insertMedia
     */

    fun afegirFila(media: Media): String {
        return if (netflixDao.getMediaById(media.getId()) != null) {
            "Error: el ID introduit ja existeix en la base de dades."
        } else {
            netflixDao.insertMedia(media)
            "La fila s'ha afegit correctament."
        }
    }

    /**
     * Funció que edita una fila de la base de dades de Netflix, si el ID existeix en la base de dades.
     * @autor KilianCuadrado
     * @version 1.0
     * @since 2026-03-11
     * @see NetflixDao.updateMedia
     */

    fun editarFila(media: Media): String {
        return if (netflixDao.updateMedia(media)) {
            "La fila amb ID ${media.getId()} s'ha actualitzat correctament."
        } else {
            "No existeix cap fila amb ID ${media.getId()}."
        }
    }

    /**
     * Funció que elimina una fila de la base de dades de Netflix, si el ID existeix en la base de dades.
     * @autor KilianCuadrado
     * @version 1.0
     * @since 2026-03-11
     * @see NetflixDao.deleteMedia
     */

    fun eliminarFila(media: Media): String {
        return if (netflixDao.getMediaById(media.getId()) != null) {
            netflixDao.deleteMedia(media)
            "La fila amb ID ${media.getId()} s'ha eliminat correctament."
        } else {
            "Error: el ID introduit no existeix en la base de dades."
        }
    }

    /**
     * Funció que elimina totes les files de la base de dades de Netflix.
     * @autor KilianCuadrado
     * @version 1.0
     * @since 2026-03-11
     * @see NetflixDao.deleteAllMedia
     */

    fun elimarTotesFilas() {
        netflixDao.deleteAllMedia()
    }

    /**
     * Funció que confirma l'eliminació de totes les files de la base de dades de Netflix, si la resposta és 'S' o 's'.
     * @autor KilianCuadrado
     * @version 1.0
     * @since 2026-03-11
     * @see NetflixDao.deleteAllMedia
     */

    fun confirmarEliminacioTotesFiles(resposta: Char): String {
        return if (resposta.equals('S', ignoreCase = true)) {
            elimarTotesFilas()
            "S'han eliminat totes les files de la base de dades."
        } else {
            "No s'han eliminat les files de la base de dades."
        }
    }

    /**
     * Busca contingut pel títol (coincidència parcial, ignorant majúscules/minúscules).
     * @param titol text parcial o complet del títol
     * @return resultats de la cerca en format text
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun buscarMediaPerTitol(titol: String): String {
        val result = netflixDao.getAllMedia().filter { it.getTitle().contains(titol, ignoreCase = true) }
        return formatMediaList(result)
    }

    /**
     * Retorna totes les pel·lícules o totes les sèries segons el tipus indicat.
     * @param type tipus de media a retornar
     * @return resultats del tipus seleccionat en format text
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun buscarMediaPerTipus(type: MediaType): String {
        return when (type) {
            MediaType.MOVIE -> formatMediaList(netflixDao.getAllMovies())
            MediaType.TV_SHOW -> formatMediaList(netflixDao.getAllSeries())
        }
    }

    /**
     * Funció que retorna un string amb totes les dades de les pel·lícules i sèries de Netflix en les que ha participat una persona, separades per línies.
     * @param persona la persona de la que es volen buscar les pel·lícules i sèries en les que ha participat, pot ser un director o un actor
     * @return un string amb totes les dades de les pel·lícules i sèries de Netflix en les que ha participat la persona, separades per línies.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     * @see Director
     * @see Actor
     */
    fun buscarMediaPerPersona(persona: Persona): String {
        val result = when (persona) {
            is Director -> {
                netflixDao.getAllMedia().filter { media ->
                    media.getDirector().any { it.getName().equals(persona.getName(), ignoreCase = true) }
                }
            }
            is Actor -> {
                netflixDao.getAllMedia().filter { media ->
                    media.getCast().any { it.getName().equals(persona.getName(), ignoreCase = true) }
                }
            }
            else -> emptyList()
        }

        return formatMediaList(result)
    }

    /**
     * Funció que retorna un string amb totes les dades de les pel·lícules i sèries de Netflix que van ser estrenades en un any concret, separades per línies.
     * @param any l'any de l'estrena de les pel·lícules i sèries que es volen buscar
     * @return un string amb totes les dades de les pel·lícules i sèries de Netflix que van ser estrenades en un any concret, separades per línies.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     * @see Media.getReleaseYear
     */

    fun buscarMediaPerAny(any: Int): String {
        val result = netflixDao.getAllMedia().filter { it.getReleaseYear() == any }
        return formatMediaList(result)
    }

    /**
     * Funció que retorna un string amb totes les dades de les pel·lícules i sèries de Netflix que tenen una valoració concreta, separades per línies.
     * @param rating la valoració de les pel·lícules i sèries que es volen buscar
     * @return un string amb totes les dades de les pel·lícules i sèries de Netflix que tenen una valoració concreta, separades per línies.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     * @see Media.getRating
     */

     fun buscarMediaPerRating(rating: MediaRating): String {
        val result = netflixDao.getAllMedia().filter { it.getRating() == rating }
        return formatMediaList(result)
    }

    /**
     * Busca contingut que contingui qualsevol dels gèneres indicats.
     * @param generes llista de gèneres de filtratge
     * @return resultats de la cerca per gènere
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
     fun buscarMediaPerGeneres(generes: MutableList<MediaGenere>): String {
        val result = netflixDao.getAllMedia().filter { media ->
            media.getListedIn().any { generes.contains(it) }
        }

        return formatMediaList(result)
    }

    /**
     * Funció que ordena les pel·lícules i sèries de Netflix per una columna específica.
     * @param columna el nom de la columna per ordenar
     * @param ascendent si és veritable, s'ordena de manera ascendent, si és fals, descendent
     * @return un string amb totes les dades de les pel·lícules i sèries de Netflix ordenades, separades per línies.
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMediaOrdered
     */

    fun ordenarMediaPerColumna(columna: String, ascendent: Boolean = true): String {
        val columnMap = mapOf(
            "id" to "show_id",
            "tipus" to "type",
            "title" to "title",
            "pais" to "country",
            "data" to "date_added",
            "any" to "release_year",
            "rating" to "rating",
            "durada" to "duration_value"
        )

        val safeColumn = columnMap[columna.lowercase().trim()] ?: "title"
        return formatMediaList(netflixDao.getAllMediaOrdered(safeColumn, ascendent))
    }

    // Opcions extra (4) relacionades amb les dades
    /**
     * Funció que compta el nombre de pel·lícules i sèries a la base de dades de Netflix.
     * @return un string amb el nombre de pel·lícules i sèries
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMovies
     * @see NetflixDao.getAllSeries
     */

    fun comptarPelliculesISeries(): String {
        val movies = netflixDao.getAllMovies().size
        val series = netflixDao.getAllSeries().size
        return "Pellicules: $movies | Series: $series"
    }

    /**
     * Funció que retorna els 5 països amb més títols disponibles a Netflix.
     * @return un string amb els 5 països amb més títols i la seva quantitat de títols
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     */

    fun top5PaisosAmbMesTitols(): String {
        val allMedia = netflixDao.getAllMedia()
        if (allMedia.isEmpty()) return "No hi ha dades."

        // Pipeline d'agregació: extreu països, normalitza text i compta freqüències per quedar-se amb el top 5.
        val top = allMedia
            .flatMap { media ->
                media.getCountry().split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)

        return if (top.isEmpty()) {
            "No hi ha paisos informats."
        } else {
            top.joinToString("\n") { "${it.key}: ${it.value}" }
        }
    }

    /**
     * Funció que determina quin any hi va haver més estrenes de pel·lícules i sèries a Netflix.
     * @return un string amb l'any amb més estrenes i la seva quantitat de títols
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     */

    fun anyAmbMesEstrenes(): String {
        val grouped = netflixDao.getAllMedia().groupingBy { it.getReleaseYear() }.eachCount()
        val best = grouped.maxByOrNull { it.value }
        return if (best == null) "No hi ha dades." else "Any amb mes estrenes: ${best.key} (${best.value} titols)"
    }

    /**
     * Funció que determina quin és el gènere més freqüent entre les pel·lícules i sèries de Netflix.
     * @return un string amb el gènere més freqüent i la seva quantitat de títols
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getAllMedia
     */

    fun genereMesFrequent(): String {
        val grouped = netflixDao.getAllMedia()
            .flatMap { it.getListedIn() }
            .groupingBy { it }
            .eachCount()

        val best = grouped.maxByOrNull { it.value }
        return if (best == null) "No hi ha dades." else "Genere mes frequent: ${best.key.value} (${best.value} titols)"
    }

    /**
     * Funció que retorna un objecte Media a partir del seu ID.
     * @param id l'ID de la pel·lícula o sèrie
     * @return l'objecte Media corresponent a l'ID, o null si no existeix
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     * @see NetflixDao.getMediaById
     */

    fun getMediaPerId(id: String): Media? {
        return netflixDao.getMediaById(id)
    }

    /**
     * Funció que crea un objecte Media a partir d'una línia de text en format CSV.
     * @param linia la línia de text en format CSV
     * @return l'objecte Media creat, o null si hi ha un error en el format
     * @author KilianCuadrado
     * @version 1.0
     * @since 2026-03-12
     */

    fun crearMediaDesDeCSV(linia: String): Media? {
        return parseMediaFromCsvLine(linia)
    }

    /**
     * Construeix un objecte `Media` a partir d'una línia CSV parsejada.
     * La funció valida mínim de camps, tipus de media i converteix valors numèrics.
     * @param line línia de text amb format CSV
     * @return instància `Pelicula`/`Series` o `null` si el format no és vàlid
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun parseMediaFromCsvLine(line: String): Media? {
        val fields = splitCsvLine(line)
        if (fields.size < 13) return null

        val id = fields[0].trim()
        val typeValue = fields[1].trim()
        val title = fields[2].trim()
        // Cada nom es normalitza (trim), es descarten buits i es transforma a objecte Director.
        val directors = fields[3]
            .split(",")
            .mapNotNull { token -> token.trim().takeIf { it.isNotEmpty() }?.let { Director(it) } }
            .toMutableList()
        // Mateixa estratègia per al repartiment: neteja + filtre + map a Actor.
        val cast = fields[4]
            .split(",")
            .mapNotNull { token -> token.trim().takeIf { it.isNotEmpty() }?.let { Actor(it) } }
            .toMutableList()
        val country = fields[5].trim()
        val dateAdded = fields[6].trim()
        val releaseYear = fields[7].trim().toIntOrNull() ?: 0
        val rating = MediaRating.fromString(fields[8].trim())
        val duration = fields[9].trim()
        // Parse dels gèneres de text a enum, evitant entrades buides o malformades.
        val generes = fields[10]
            .split(",")
            .mapNotNull { token -> token.trim().takeIf { it.isNotEmpty() }?.let { MediaGenere.fromString(it) } }
            .toMutableList()
        val description = fields[11].trim()
        val durationValue = fields[12].trim().toIntOrNull() ?: 0

        val mediaType = when {
            typeValue.equals(MediaType.MOVIE.value, ignoreCase = true) -> MediaType.MOVIE
            typeValue.equals(MediaType.TV_SHOW.value, ignoreCase = true) -> MediaType.TV_SHOW
            else -> null
        } ?: return null

        return if (mediaType == MediaType.MOVIE) {
            Pelicula(
                id,
                title,
                directors,
                cast,
                country,
                dateAdded,
                releaseYear,
                rating,
                duration,
                generes,
                description,
                durationValue
            )
        } else {
            Series(
                id,
                title,
                directors,
                cast,
                country,
                dateAdded,
                releaseYear,
                rating,
                duration,
                generes,
                description,
                durationValue
            )
        }
    }

    /**
     * Divideix una línia CSV respectant cometes escapades i comes dins de camps.
     * Implementació manual per evitar trencar descripcions amb comes.
     * @param line línia CSV completa
     * @return llista ordenada de camps de la línia
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        // Petit parser d'estat: respecta comes dins cometes i cometes escapades "".
        while (i < line.length) {
            val c = line[i]

            when {
                c == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    current.append('"')
                    i++
                }
                c == '"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current.setLength(0)
                }
                else -> current.append(c)
            }
            i++
        }

        result.add(current.toString())
        return result
    }
}