package model

/**
 * Enum que representa els diferents gèneres de les pel·lícules i sèries de Netflix
 * Cada gènere té un valor string associat per a una fàcil identificació.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Media
 */
enum class MediaGenere(
    val value: String
) {
    ACTION_AND_ADVENTURE("Action & Adventure"),
    ANIME_FEATURES("Anime Features"),
    ANIME_SERIES("Anime Series"),
    BRITISH_TV_SHOWS("British TV Shows"),
    CHILDREN_AND_FAMILY_MOVIES("Children & Family Movies"),
    CLASSIC_AND_CULT_TV("Classic & Cult TV"),
    CLASSIC_MOVIES("Classic Movies"),
    COMEDIES("Comedies"),
    CRIME_TV_SHOWS("Crime TV Shows"),
    CULT_MOVIES("Cult Movies"),
    DOCUMENTARIES("Documentaries"),
    DOCUSERIES("Docuseries"),
    DRAMAS("Dramas"),
    FAITH_AND_SPIRITUALITY("Faith & Spirituality"),
    HORROR_MOVIES("Horror Movies"),
    INDEPENDENT_MOVIES("Independent Movies"),
    INTERNATIONAL_MOVIES("International Movies"),
    INTERNATIONAL_TV_SHOWS("International TV Shows"),
    KIDS_TV("Kids' TV"),
    KOREAN_TV_SHOWS("Korean TV Shows"),
    LGBTQ_MOVIES("LGBTQ Movies"),
    MOVIES("Movies"),
    MUSIC_AND_MUSICALS("Music & Musicals"),
    REALITY_TV("Reality TV"),
    ROMANTIC_MOVIES("Romantic Movies"),
    ROMANTIC_TV_SHOWS("Romantic TV Shows"),
    SCI_FI_AND_FANTASY("Sci-Fi & Fantasy"),
    SCIENCE_AND_NATURE_TV("Science & Nature TV"),
    SPANISH_LANGUAGE_TV_SHOWS("Spanish-Language TV Shows"),
    SPORTS_MOVIES("Sports Movies"),
    STAND_UP_COMEDY("Stand-Up Comedy"),
    STAND_UP_COMEDY_AND_TALK_SHOWS("Stand-Up Comedy & Talk Shows"),
    TV_ACTION_AND_ADVENTURE("TV Action & Adventure"),
    TV_COMEDIES("TV Comedies"),
    TV_DRAMAS("TV Dramas"),
    TV_HORROR("TV Horror"),
    TV_MYSTERIES("TV Mysteries"),
    TV_SCI_FI_AND_FANTASY("TV Sci-Fi & Fantasy"),
    TV_SHOWS("TV Shows"),
    TV_THRILLERS("TV Thrillers"),
    TEEN_TV_SHOWS("Teen TV Shows"),
    THRILLERS("Thrillers"),
    UNKNOWN("Unknown");

    companion object {

        /**
         * Funció que retorna el MediaGenere corresponent a un string, si no existeix retorna UNKNOWN
         * @param valor el string a convertir a MediaGenere
         * @return el MediaGenere corresponent al string, o UNKNOWN si no existeix
         * @author Raimon Izard
         * @version 1.0
         * @since 2026-03-11
         */

        fun fromString(valor: String): MediaGenere {

            var mediaGenere: MediaGenere?

            mediaGenere = MediaGenere.entries.find { it.value.equals(valor, ignoreCase = true) }

            if (mediaGenere == null){

                mediaGenere = MediaGenere.UNKNOWN

            }

            return mediaGenere
        }
    }
}