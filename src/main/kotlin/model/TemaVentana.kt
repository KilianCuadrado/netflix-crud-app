package model

enum class TemaVentana(
    val colorFondo: String,
    val colorTexto: String,
    val colorBotons: String,
    val colorBotonTema: String
) {
    NEGRE("#121212", "#E0E0E0", "#222222",""),
    CLAR("#D4FFFD", "#212121", "#D9DAFF","") //Mirar colores adecuados
}