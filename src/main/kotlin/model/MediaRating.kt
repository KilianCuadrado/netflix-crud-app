package model

/**
 * Enumeració que representa la classificació d'edat d'un contingut.
 * Conté els valors: G, PG, PG-13, R, NC-17, NR, UR, TV-G, TV-PG, TV-14,
 * TV-MA, TV-Y, TV-Y7, TV-Y7-FV i UNKNOWN.
 * També inclou un `companion object` amb una funció per convertir un text a `MediaRating`.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 */
enum class MediaRating (
    val value: String
){
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17"),
    NR("NR"),
    UR("UR"),
    TV_G("TV-G"),
    TV_PG("TV-PG"),
    TV_14("TV-14"),
    TV_MA("TV-MA"),
    TV_Y("TV-Y"),
    TV_Y7("TV-Y7"),
    TV_Y7_FV("TV-Y7-FV"),
    UNKNOWN("Unknown");


    companion object {

        /**
         * Funció que retorna el MediaRating corresponent a un string, si no existeix retorna UNKNOWN
         * @param valor el string a convertir a MediaRating
         * @return el MediaRating corresponent al string, o UNKNOWN si no existeix
         * @author Raimon Izard
         * @version 1.0
         * @since 2026-03-11
         */

        fun fromString(valor: String): MediaRating {

            var mediaRating: MediaRating?

            mediaRating = MediaRating.entries.find { it.value.equals(valor, ignoreCase = true) }

            if (mediaRating == null){

                mediaRating = UNKNOWN

            }

            return mediaRating
        }
    }
}