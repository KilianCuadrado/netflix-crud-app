package model

/**
 * Enumeració que representa el tipus de contingut: pel·lícula o sèrie.
 * Cada tipus té un valor de text associat per facilitar-ne la identificació.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Pelicula
 * @see Series
 * @see Media
 */

enum class MediaType(val value: String) {
    MOVIE("Movie"),
    TV_SHOW("TV Show")
}