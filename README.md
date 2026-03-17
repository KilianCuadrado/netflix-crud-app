# A48 - Acces a BD en local 🎬

Aplicacio Kotlin + JavaFX amb arquitectura MVC per consultar i gestionar dades de Netflix des d'una base de dades SQLite local.

## Dataset (Kaggle)
- https://www.kaggle.com/datasets/youssefkhaled117/netflix-titles-cleaned?select=netflix_titles_cleaned.csv

## Tecnologies
- Kotlin (JVM)
- JavaFX
- SQLite (jdbc)
- Gradle

## Funcionalitats principals
- Veure totes les dades
- Veure les dades ordenades per columna
- Buscar dades per diferents camps (titol, tipus, director, actor, any, rating, genere)
- Exportar dades a fitxer `csv`, `txt` o `bin`
- Afegir una fila manualment
- Importar dades des de fitxer de text (`txt/csv`) o binari (`bin`)
- Editar dades
- Eliminar una fila
- Eliminar totes les dades
- Sortir de l'aplicacio

## 4 opcions extra afegides
- Comptar pellicules i series
- Top 5 paisos amb mes titols
- Any amb mes estrenes
- Genere mes frequent

## Execucio
```bash
./gradlew run
```

## Notes
- Base de dades local: `src/main/kotlin/database/netflix.db`
- Fitxers exportats: carpeta `files/`
- Interficie amb mode clar/fosc 🌗
