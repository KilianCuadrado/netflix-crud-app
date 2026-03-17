package model

/**
 * Classe que representa una sèrie de televisió, que és un tipus de contingut.
 * Hereta de la classe Media i té el tipus específic MediaType.TV_SHOW.
 * @author KilianCuadrado
 * @version 2.0
 * @since 2026-03-11
 * @see Media
 * @see MediaType
 * @see MediaRating
 * @see MediaGenere
 * @constructor Crea una nova sèrie amb les propietats indicades.
 */

class Series: Media{
    constructor(
        id: String,
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
    ) : super(
        id,
        type = MediaType.TV_SHOW,
        title,
        director,
        cast,
        country,
        dateAdded,
        releaseYear,
        rating,
        duration,
        listedIn,
        description,
        durationValue
    )

}