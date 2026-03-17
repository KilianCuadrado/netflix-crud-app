package controller

import dao.INetflixDao
import model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Netflix Controller Tests")
class NetflixControllerTest {

    private lateinit var controller: NetflixController
    private lateinit var dao: InMemoryNetflixDao

    private class InMemoryNetflixDao(initial: List<Media>) : INetflixDao {
        private val data = initial.toMutableList()

        override fun getAllMedia(): MutableList<Media> = data.toMutableList()

        override fun getAllMediaOrdered(column: String, ascending: Boolean): MutableList<Media> {
            val sorted = data.sortedWith(compareBy<Media> {
                when (column) {
                    "show_id" -> it.getId()
                    "type" -> it.getType().value
                    "title" -> it.getTitle()
                    "country" -> it.getCountry()
                    "date_added" -> it.getDateAdded()
                    "release_year" -> it.getReleaseYear()
                    "rating" -> it.getRating().value
                    "duration" -> it.getDuration()
                    "duration_value" -> it.getDurationValue()
                    else -> it.getTitle()
                }
            })
            return if (ascending) sorted.toMutableList() else sorted.reversed().toMutableList()
        }

        override fun getMediaById(id: String): Media? = data.firstOrNull { it.getId() == id }

        override fun getAllSeries(): List<Series> = data.filterIsInstance<Series>()

        override fun getAllMovies(): List<Pelicula> = data.filterIsInstance<Pelicula>()

        override fun deleteAllMedia() {
            data.clear()
        }

        override fun insertMedia(media: Media) {
            data.add(media)
        }

        override fun updateMedia(media: Media): Boolean {
            val index = data.indexOfFirst { it.getId() == media.getId() }
            if (index < 0) return false
            data[index] = media
            return true
        }

        override fun deleteMedia(media: Media) {
            data.removeIf { it.getId() == media.getId() }
        }
    }

    @BeforeEach
    fun setUp() {
        dao = InMemoryNetflixDao(
            listOf(
                createTestPelicula("m1", "Matrix", 1999),
                createTestPelicula("m2", "Inception", 2010),
                createTestSeries("s1", "Breaking Bad", 2008)
            )
        )
        controller = NetflixController(dao)
    }

    @Test
    @DisplayName("Should retrieve all media using controller")
    fun testGetAllMedia() {
        val result = controller.totesDadesMedia()

        assertTrue(result.contains("Matrix"))
        assertTrue(result.contains("Inception"))
        assertTrue(result.contains("Breaking Bad"))
    }

    @Test
    @DisplayName("Should retrieve movies only")
    fun testGetAllMovies() {
        val result = controller.buscarMediaPerTipus(MediaType.MOVIE)

        assertTrue(result.contains("Matrix"))
        assertTrue(result.contains("Inception"))
        assertTrue(!result.contains("Breaking Bad"))
    }

    @Test
    @DisplayName("Should retrieve series only")
    fun testGetAllSeries() {
        val result = controller.buscarMediaPerTipus(MediaType.TV_SHOW)

        assertTrue(result.contains("Breaking Bad"))
        assertTrue(!result.contains("Matrix"))
    }

    @Test
    @DisplayName("Should retrieve media sorted by title ascending")
    fun testGetMediaSortedAscending() {
        val result = controller.ordenarMediaPerColumna("title", true)

        val inceptionIndex = result.indexOf("Inception")
        val matrixIndex = result.indexOf("Matrix")
        assertTrue(inceptionIndex >= 0)
        assertTrue(matrixIndex >= 0)
        assertTrue(inceptionIndex < matrixIndex)
    }

    @Test
    @DisplayName("Should retrieve media by ID")
    fun testGetMediaById() {
        val result = controller.getMediaPerId("m1")

        assertNotNull(result)
        assertEquals("Matrix", result.getTitle())
        assertEquals("m1", result.getId())
    }

    @Test
    @DisplayName("Should insert new media")
    fun testInsertMedia() {
        val newMovie = createTestPelicula("m9", "Parasite", 2019)

        val message = controller.afegirFila(newMovie)

        assertTrue(message.contains("afegit correctament"))
        assertNotNull(controller.getMediaPerId("m9"))
    }

    @Test
    @DisplayName("Should reject duplicate ID on insert")
    fun testInsertMediaDuplicate() {
        val duplicateMovie = createTestPelicula("m1", "Other Matrix", 1999)

        val message = controller.afegirFila(duplicateMovie)

        assertTrue(message.contains("ja existeix"))
    }

    @Test
    @DisplayName("Should update existing media")
    fun testUpdateMedia() {
        val movieToUpdate = createTestPelicula("m1", "Updated Title", 1999)

        val message = controller.editarFila(movieToUpdate)

        assertTrue(message.contains("actualitzat correctament"))
        assertEquals("Updated Title", controller.getMediaPerId("m1")?.getTitle())
    }

    @Test
    @DisplayName("Should delete media")
    fun testDeleteMedia() {
        val existingMovie = createTestPelicula("m2", "Inception", 2010)

        val message = controller.eliminarFila(existingMovie)

        assertTrue(message.contains("eliminat correctament"))
        assertEquals(null, controller.getMediaPerId("m2"))
    }

    @Test
    @DisplayName("Should delete all media")
    fun testDeleteAllMedia() {
        controller.elimarTotesFilas()

        assertTrue(controller.totesDadesMedia().contains("No s'han trobat dades."))
    }

    private fun createTestPelicula(id: String, title: String, releaseYear: Int): Pelicula {
        return Pelicula(
            id = id,
            title = title,
            director = mutableListOf(Director("Test Director")),
            cast = mutableListOf(Actor("Test Actor")),
            country = "USA",
            dateAdded = "2024-01-01",
            releaseYear = releaseYear,
            rating = MediaRating.PG_13,
            duration = "120 min",
            listedIn = mutableListOf(MediaGenere.DOCUMENTARIES),
            description = "Test description",
            durationValue = 120
        )
    }

    private fun createTestSeries(id: String, title: String, releaseYear: Int): Series {
        return Series(
            id = id,
            title = title,
            director = mutableListOf(Director("Test Director")),
            cast = mutableListOf(Actor("Test Actor")),
            country = "USA",
            dateAdded = "2024-01-01",
            releaseYear = releaseYear,
            rating = MediaRating.TV_MA,
            duration = "3 Seasons",
            listedIn = mutableListOf(MediaGenere.TV_DRAMAS),
            description = "Test series description",
            durationValue = 3
        )
    }
}
