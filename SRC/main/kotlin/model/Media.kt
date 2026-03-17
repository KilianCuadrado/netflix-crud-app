package model

import java.io.Serializable

/**
 * Classe abstracta que representa un contingut multimèdia, que pot ser una pel·lícula o una sèrie.
 * Conté propietats i mètodes comuns per als dos tipus de contingut.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Pelicula
 * @see Series
 * @see MediaType
 * @see MediaRating
 * @see MediaGenere
 * @constructor Crea un nou contingut amb les propietats indicades.
 * @param id identificador únic del contingut.
 * @param type tipus de contingut (pel·lícula o sèrie).
 * @param title títol del contingut.
 * @param director directors del contingut.
 * @param cast repartiment del contingut.
 * @param country país d'origen del contingut.
 * @param dateAdded data d'alta del contingut a la base de dades.
 * @param releaseYear any d'estrena del contingut.
 * @param rating classificació d'edat del contingut.
 * @param duration durada en format text (per exemple, "90 min" o "2 Seasons").
 * @param listedIn llista de gèneres del contingut.
 * @param description descripció breu del contingut.
 * @param durationValue durada en format numèric (minuts o temporades).
 */
open abstract class Media : Serializable {

    private val id: String
    private val type: MediaType
    private val title: String
    private val director: MutableList<Director>
    private val cast: MutableList<Actor>
    private val country: String
    private val dateAdded: String
    private val releaseYear: Int
    private val rating: MediaRating
    private val duration: String
    private val listedIn: MutableList<MediaGenere>
    private val description: String
    private val durationValue: Int

    constructor(
        id: String,
        type: MediaType,
        title: String,
        director: MutableList<Director>,
        cast: MutableList<Actor>,
        country: String,
        dateAdded: String,
        releaseYear: Int,
        rating: MediaRating,
        duration: String,
        listedIn: MutableList<MediaGenere>,
        description: String,
        durationValue: Int
    ) {
        this.id = id
        this.type = type
        this.title = title
        this.director = director
        this.cast = cast
        this.country = country
        this.dateAdded = dateAdded
        this.releaseYear = releaseYear
        this.rating = rating
        this.duration = duration
        this.listedIn = listedIn
        this.description = description
        this.durationValue = durationValue
    }

    fun getId(): String {
        return id
    }

    fun getType(): MediaType {
        return type
    }

    fun getTitle(): String {
        return title
    }

    fun getDirector(): MutableList<Director> {
        return director
    }

    fun getCast(): MutableList<Actor> {
        return cast
    }

    fun getCountry(): String {
        return country
    }

    fun getDateAdded(): String {
        return dateAdded
    }

    fun getReleaseYear(): Int {
        return releaseYear
    }

    fun getRating(): MediaRating {
        return rating
    }

    fun getDuration(): String {
        return duration
    }

    fun getListedIn(): MutableList<MediaGenere> {
        return listedIn
    }

    fun getDescription(): String {
        return description
    }

    fun getDurationValue(): Int {
        return durationValue
    }

    /**
     * Converteix el contingut en una línia CSV.
     */
    fun toCSV(): String {
        fun esc(value: String): String {
            return "\"${value.replace("\"", "\"\"")}\""
        }

        return listOf(
            esc(id),
            esc(type.value),
            esc(title),
            esc(director.joinToString(",") { it.getName() }),
            esc(cast.joinToString(",") { it.getName() }),
            esc(country),
            esc(dateAdded),
            esc(releaseYear.toString()),
            esc(rating.value),
            esc(duration),
            esc(listedIn.joinToString(",") { it.value }),
            esc(description),
            esc(durationValue.toString())
        ).joinToString(",")
    }

    /**
     * Dona format a la descripció inserint salts de línia cada cert nombre de paraules.
     */
    fun netejarDescripcio(maximPalabrasLinea: Int): String {
        var contPal = 0
        var descripcioNetejada = ""

        for (letra in this.description) {
            if (letra == ' ') {
                contPal++
                if (contPal > maximPalabrasLinea) {
                    descripcioNetejada += "\n"
                    contPal = 0
                } else {
                    descripcioNetejada += letra
                }
            } else {
                descripcioNetejada += letra
            }
        }
        return descripcioNetejada
    }

    override fun toString(): String {
        return "id=$id \ntype=$type \ntitle=$title \ndirector/s=${director.joinToString(",") { it.getName() }} \ncasting=${cast.joinToString(",") { it.getName() }} \ncountry=$country \ndateAdded=$dateAdded \nreleaseYear=$releaseYear \nrating=$rating \nduration=$duration \nlistedIn=${listedIn.joinToString(",") { it.value }} \ndescription=${netejarDescripcio(10)} \ndurationValue=$durationValue \n"
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}