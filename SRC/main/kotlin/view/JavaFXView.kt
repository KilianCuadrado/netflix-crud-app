package view

import controller.NetflixController
import dao.INetflixDao
import dao.NetflixDao
import database.DatabaseManager
import database.SupabaseManager
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ChoiceDialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextInputDialog
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage
import model.Actor
import model.Director
import model.MediaGenere
import model.MediaRating
import model.MediaType
import model.TemaVentana
import java.sql.Connection

// 1. Definim les connexions a BD local i remota
val conLocal: Connection = DatabaseManager.connection
val conRemot: Connection = SupabaseManager.connection

// 2. Instanciem el DAO amb la connexió desitjada
val dao: INetflixDao = NetflixDao(conRemot)

// 3. Creem el controlador d'usuaris passant-li el dao creat
val netflixController: NetflixController = NetflixController(dao)


class JavaFXView: Application() {

    private fun askText(title: String, prompt: String): String? {
        val dialog = TextInputDialog()
        dialog.title = title
        dialog.headerText = prompt
        dialog.contentText = "Valor:"
        return dialog.showAndWait().orElse(null)
    }

    private fun askChoice(title: String, prompt: String, options: List<String>): String? {
        val dialog = ChoiceDialog(options.firstOrNull() ?: "", options)
        dialog.title = title
        dialog.headerText = prompt
        return dialog.showAndWait().orElse(null)
    }

    private fun showInfo() {
        Alert(Alert.AlertType.INFORMATION, "Fins aviat!").showAndWait()
    }

    override fun start(stage: Stage) {

        /**
         * Variables de la primera interfície.
         */
        // Ajuste del Título para que parezca un título real
        val titleLabel = Label("¿Qué deseas hacer?")
        titleLabel.style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;"

        val outputArea = TextArea().apply {
            isEditable = false
            isWrapText = true
            prefRowCount = 16
        }

        val botonAll = Button("Ver todos los datos")
        val botonOrdenColumnas = Button("Ver los datos ordenados por alguna columna")
        val botonBuscar = Button("Buscar datos por alguna columna")
        val botonAddDatos = Button("Añadir datos")
        val botonEdit = Button("Editar datos")
        val botonEliminar = Button("Eliminar datos")
        val botonAllEliminar = Button("Eliminar todos los datos")
        val botonExportar = Button("Exportar datos")
        val botonImportarTxt = Button("Importar datos TXT/CSV")
        val botonImportarBin = Button("Importar datos BIN")
        val botonExtra1 = Button("Extra: contar pelis/series")
        val botonExtra2 = Button("Extra: top 5 paises")
        val botonExtra3 = Button("Extra: año con mas estrenos")
        val botonExtra4 = Button("Extra: genero mas frecuente")

        // Botón Salir ajustado proporcionalmente al texto
        val botonSalir = Button("Salir")
        botonSalir.setPrefSize(90.0, 40.0)
        botonSalir.style = "-fx-font-weight: bold; -fx-font-size: 13px;"

        val botonTema = ToggleButton()

        // Efecto Hover con espaciado real para no solapar
        val botonesLilas = listOf(
            botonAll, botonOrdenColumnas, botonBuscar, botonAddDatos, botonEdit,
            botonEliminar, botonAllEliminar, botonExportar, botonImportarTxt,
            botonImportarBin, botonExtra1, botonExtra2, botonExtra3, botonExtra4
        )

        botonesLilas.forEach { btn ->
            btn.setOnMouseEntered {
                btn.style += "-fx-font-size: 15px; -fx-font-weight: bold;"
                HBox.setMargin(btn, Insets(0.0, 15.0, 0.0, 15.0))
            }
            btn.setOnMouseExited {
                btn.style = btn.style.replace("-fx-font-size: 15px; -fx-font-weight: bold;", "")
                HBox.setMargin(btn, Insets(0.0))
            }
        }

        /**
         * Estructura de filas
         */
        val topRow = HBox(8.0, botonAll, botonOrdenColumnas, botonBuscar)
        val midRow = HBox(8.0, botonAddDatos, botonEdit, botonEliminar, botonAllEliminar)
        val ioRow = HBox(8.0, botonExportar, botonImportarTxt, botonImportarBin)
        val extraRow = HBox(8.0, botonExtra1, botonExtra2, botonExtra3, botonExtra4)

        val spacer = Region()
        HBox.setHgrow(spacer, Priority.ALWAYS)

        // Botón tema cuadrado para el icono
        botonTema.setPrefSize(60.0, 60.0)

        val mainFooter = HBox(botonSalir, spacer, botonTema)
        mainFooter.alignment = Pos.BOTTOM_CENTER
        mainFooter.padding = Insets(15.0, 0.0, 0.0, 0.0)

        val root = VBox(10.0, titleLabel, topRow, midRow, ioRow, extraRow, outputArea, mainFooter)
        VBox.setVgrow(outputArea, Priority.ALWAYS)

        // Estilos iniciales (Tema Claro por defecto)
        root.style = "-fx-background-color: ${TemaVentana.CLAR.colorFondo};"
        root.lookupAll(".button").forEach { node ->
            val b = node as Button
            val extraStyle = if (b == botonSalir) "-fx-font-weight: bold; -fx-font-size: 13px;" else ""
            b.style += "-fx-text-fill: ${TemaVentana.CLAR.colorTexto}; -fx-background-color: ${TemaVentana.CLAR.colorBotons}; $extraStyle"
        }
        outputArea.style = "-fx-control-inner-background: ${TemaVentana.CLAR.colorBotons}; -fx-text-fill: ${TemaVentana.CLAR.colorTexto};"

        /**
         * Lógica de Botones (Igual al original)
         */
        botonAll.setOnAction { outputArea.text = netflixController.totesDadesMedia() }
        botonOrdenColumnas.setOnAction {
            val col = askChoice("Ordenar", "Columna", listOf("title", "id", "tipus", "pais", "data", "any", "rating", "durada")) ?: return@setOnAction
            val ord = askChoice("Orden", "Orden", listOf("ASC", "DESC")) ?: return@setOnAction
            outputArea.text = netflixController.ordenarMediaPerColumna(col, ord == "ASC")
        }
        botonBuscar.setOnAction {
            val tipus = askChoice("Buscar", "Tipo", listOf("titulo", "tipo", "director", "actor", "año", "rating", "genero")) ?: return@setOnAction
            outputArea.text = when (tipus) {
                "titulo" -> netflixController.buscarMediaPerTitol(askText("Buscar", "Título") ?: "")
                "tipo" -> netflixController.buscarMediaPerTipus(if (askChoice("Tipo", "Selecciona", listOf("Movie", "TV Show")) == "Movie") MediaType.MOVIE else MediaType.TV_SHOW)
                "director" -> netflixController.buscarMediaPerPersona(Director(askText("Director", "Nombre") ?: ""))
                "actor" -> netflixController.buscarMediaPerPersona(Actor(askText("Actor", "Nombre") ?: ""))
                "año" -> netflixController.buscarMediaPerAny(askText("Año", "Año")?.toIntOrNull() ?: 0)
                "rating" -> netflixController.buscarMediaPerRating(MediaRating.fromString(askText("Rating", "Valor") ?: ""))
                "genero" -> netflixController.buscarMediaPerGeneres(mutableListOf(MediaGenere.fromString(askText("Género", "Valor") ?: "")))
                else -> ""
            }
        }
        botonAddDatos.setOnAction {
            val line = askText("Añadir", "Línea CSV") ?: return@setOnAction
            outputArea.text = netflixController.crearMediaDesDeCSV(line)?.let { netflixController.afegirFila(it) } ?: "Error"
        }
        botonEdit.setOnAction {
            val line = askText("Editar", "Línea CSV") ?: return@setOnAction
            outputArea.text = netflixController.crearMediaDesDeCSV(line)?.let { netflixController.editarFila(it) } ?: "Error"
        }
        botonEliminar.setOnAction {
            val id = askText("Eliminar", "ID") ?: return@setOnAction
            outputArea.text = netflixController.getMediaPerId(id)?.let { netflixController.eliminarFila(it) } ?: "No existe"
        }
        botonAllEliminar.setOnAction {
            if (askChoice("Confirmar", "¿Borrar todo?", listOf("S", "N")) == "S") outputArea.text = netflixController.confirmarEliminacioTotesFiles('S')
        }
        botonExportar.setOnAction {
            outputArea.text = netflixController.exportarDadesAFitxer(askText("Exportar", "Nombre") ?: "export", askChoice("Formato", "Tipo", listOf("csv", "txt", "bin")) ?: "csv", null)
        }
        botonImportarTxt.setOnAction { outputArea.text = netflixController.importarDadesDesDeText(askText("Importar", "Ruta") ?: "") }
        botonImportarBin.setOnAction { outputArea.text = netflixController.importarDadesDesDeBin(askText("Importar", "Ruta") ?: "") }
        botonExtra1.setOnAction { outputArea.text = netflixController.comptarPelliculesISeries() }
        botonExtra2.setOnAction { outputArea.text = netflixController.top5PaisosAmbMesTitols() }
        botonExtra3.setOnAction { outputArea.text = netflixController.anyAmbMesEstrenes() }
        botonExtra4.setOnAction { outputArea.text = netflixController.genereMesFrequent() }
        botonSalir.setOnAction { showInfo(); stage.close() }

        /**
         * Gestión del Tema
         */
        val resSol = JavaFXView::class.java.getResource("/light-mode.png")
        val resLuna = JavaFXView::class.java.getResource("/dark-mode.png")

        if (resSol != null && resLuna != null) {
            val iconLight = ImageView(Image(resSol.toExternalForm())).apply { fitWidth = 30.0; isPreserveRatio = true }
            val iconNight = ImageView(Image(resLuna.toExternalForm())).apply { fitWidth = 30.0; isPreserveRatio = true }

            botonTema.graphic = iconNight
            botonTema.style = "-fx-background-radius: 15; -fx-background-color: #FFAF7A;"

            botonTema.setOnAction {
                val tema = if (botonTema.isSelected) {
                    botonTema.graphic = iconLight
                    botonTema.style = "-fx-background-radius: 15; -fx-background-color: #b20710;"
                    TemaVentana.NEGRE
                } else {
                    botonTema.graphic = iconNight
                    botonTema.style = "-fx-background-radius: 15; -fx-background-color: #FFAF7A;"
                    TemaVentana.CLAR
                }

                root.style = "-fx-background-color: ${tema.colorFondo};"
                titleLabel.style = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 0 0 10 0; -fx-text-fill: ${tema.colorTexto};"
                root.lookupAll(".button").forEach { node ->
                    val b = node as Button
                    val extra = if (b == botonSalir) "-fx-font-weight: bold; -fx-font-size: 13px;" else ""
                    b.style = "-fx-text-fill: ${tema.colorTexto}; -fx-background-color: ${tema.colorBotons}; $extra"
                }
                outputArea.style = "-fx-control-inner-background: ${tema.colorBotons}; -fx-text-fill: ${tema.colorTexto};"
            }
        }

        root.padding = Insets(20.0)
        val scene = Scene(root, 1150.0, 720.0)
        stage.title = "Netflix"
        stage.scene = scene
        stage.show()
    }
}