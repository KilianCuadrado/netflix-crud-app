package model

import controller.NetflixController
import dao.INetflixDao
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Media Class Tests with CSV dataset")
class MediaTest {

    private lateinit var pelicula: Pelicula
    private lateinit var series: Series
    private lateinit var controller: NetflixController

    // DAO de pruebas: solo se usa para construir el controller y reutilizar su parser CSV.
    private class TestDao : INetflixDao {
        override fun getAllMedia(): MutableList<Media> = mutableListOf()
        override fun getAllMediaOrdered(column: String, ascending: Boolean): MutableList<Media> = mutableListOf()
        override fun getMediaById(id: String): Media? = null
        override fun getAllSeries(): List<Series> = emptyList()
        override fun getAllMovies(): List<Pelicula> = emptyList()
        override fun deleteAllMedia() = Unit
        override fun insertMedia(media: Media) = Unit
        override fun updateMedia(media: Media): Boolean = false
        override fun deleteMedia(media: Media) = Unit
    }

    @BeforeEach
    fun setUp() {
        controller = NetflixController(TestDao())

        val csvFile = File("src/main/kotlin/database/netflix_titles_cleaned.csv")
        assertTrue(csvFile.exists(), "No se encontro el CSV de datos: ${csvFile.path}")

        val parsedMedia = csvFile.useLines { lines: Sequence<String> ->
            lines
                .drop(1) // ignora cabecera
                .mapNotNull { controller.crearMediaDesDeCSV(it) }
                .take(300)
                .toList()
        }

        val loadedMovie = parsedMedia.filterIsInstance<Pelicula>().firstOrNull()
        assertNotNull(loadedMovie, "No se encontro ninguna pelicula valida en el CSV")
        pelicula = loadedMovie

        val loadedSeries = parsedMedia.filterIsInstance<Series>().firstOrNull()
        assertNotNull(loadedSeries, "No se encontro ninguna serie valida en el CSV")
        series = loadedSeries
    }

    @Test
    @DisplayName("Should load movie and series from real CSV data")
    fun testLoadFromCsv() {
        assertTrue(pelicula.getId().isNotBlank())
        assertTrue(series.getId().isNotBlank())
        assertEquals(MediaType.MOVIE, pelicula.getType())
        assertEquals(MediaType.TV_SHOW, series.getType())
    }

    @Test
    @DisplayName("Should get title correctly")
    fun testGetTitle() {
        assertTrue(pelicula.getTitle().isNotBlank())
        assertTrue(series.getTitle().isNotBlank())
    }

    @Test
    @DisplayName("Should get release year in a valid range")
    fun testGetReleaseYear() {
        assertTrue(pelicula.getReleaseYear() in 1900..2100)
        assertTrue(series.getReleaseYear() in 1900..2100)
    }

    @Test
    @DisplayName("Should get non-empty director and cast lists")
    fun testGetDirectorAndCast() {
        assertTrue(pelicula.getDirector().isNotEmpty())
        assertTrue(pelicula.getCast().isNotEmpty())
    }

    @Test
    @DisplayName("Should convert to CSV and parse back preserving key fields")
    fun testToCSVRoundTrip() {
        val csv = pelicula.toCSV()
        val reparsed = controller.crearMediaDesDeCSV(csv)

        assertNotNull(reparsed)
        assertEquals(pelicula.getId(), reparsed.getId())
        assertEquals(pelicula.getType(), reparsed.getType())
        assertEquals(pelicula.getTitle(), reparsed.getTitle())
        assertEquals(pelicula.getDurationValue(), reparsed.getDurationValue())
    }

    @Test
    @DisplayName("Should format description with line breaks")
    fun testFormatDescription() {
        val formatted = pelicula.netejarDescripcio(5)
        assertTrue(formatted.isNotBlank())
        assertEquals(pelicula.getDescription(), formatted.replace("\n", " "))
    }

    @Test
    @DisplayName("Should get genres/listedIn correctly")
    fun testGetGenres() {
        val genres = pelicula.getListedIn()
        assertTrue(genres.isNotEmpty())
        assertTrue(genres.all { it != MediaGenere.UNKNOWN })
    }
}
