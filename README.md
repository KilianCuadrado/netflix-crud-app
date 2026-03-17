# Netflix CRUD Database Manager

> A comprehensive Kotlin + JavaFX desktop application for managing Netflix media database with full CRUD operations, multi-database support, and advanced filtering capabilities.

## 📋 Overview

Educational project demonstrating enterprise-level backend patterns in Kotlin: MVC architecture, DAO design pattern, database abstraction, and data persistence. Manage Netflix titles (movies and series) with a desktop GUI.

## ✨ Features Implemented

### ✅ Core CRUD Operations
- **Read** - View all media with sorting and pagination
- **Create** - Manually add new movies or series
- **Update** - Edit existing media entries
- **Delete** - Remove single or all media entries

### ✅ Database Operations
- **Sort & Filter** - Order by any column (ascending/descending)
- **Search** - Query by multiple fields (title, type, director, actor, year, rating, genre)
- **Get by ID** - Direct lookup of specific media
- **Separate Queries** - Filter movies vs. series separately

### ✅ Data Management
- **Export** - Save to CSV, TXT, or Binary format
- **Import** - Load data from TXT/CSV or binary files
- **Data Validation** - Proper handling of complex data types

### ✅ Advanced Analytics
- Count movies vs. series
- Top 5 countries by media count
- Year with most releases
- Most frequent genre

### ✅ User Interface
- **JavaFX GUI** - Desktop application with modern controls
- **Theme Support** - Light/Dark mode toggle
- **Text Output Area** - Real-time results display
- **Dialog Boxes** - Input, choice, and alert dialogs
- **Responsive Layout** - VBox/HBox with proper spacing

### ✅ Multi-Database Support
- **SQLite** - Local file-based database
- **PostgreSQL (Supabase)** - Remote cloud database
- **Configurable Connections** - Switch between local and remote with single variable

## 🛠️ Tech Stack

- **Language:** Kotlin 1.9.21
- **UI Framework:** JavaFX 20
- **Databases:** SQLite 3.36.0 • PostgreSQL 42.7.3
- **Build System:** Gradle 8.x
- **Architecture:** MVC + DAO Pattern
- **Java Version:** JDK 20

## 📁 Project Structure

```
netflix-crud-app/
├── SRC/main/kotlin/
│   ├── Main.kt                      (Entry point - launches JavaFX)
│   │
│   ├── view/
│   │   └── JavaFXView.kt           (GUI - all controls and layouts)
│   │
│   ├── controller/
│   │   └── NetflixController.kt    (Business logic - operations handler)
│   │
│   ├── dao/
│   │   ├── INetflixDao.kt          (DAO interface - contracts)
│   │   └── NetflixDao.kt           (DAO implementation - SQL queries)
│   │
│   ├── database/
│   │   ├── DatabaseManager.kt      (SQLite connection handler)
│   │   └── SupabaseManager.kt      (PostgreSQL/Supabase handler)
│   │
│   └── model/
│       ├── Media.kt                 (Abstract base class)
│       ├── Pelicula.kt              (Movie - extends Media)
│       ├── Series.kt                (Series - extends Media)
│       ├── Persona.kt               (Base for Actor/Director)
│       ├── Actor.kt                 (Cast member)
│       ├── Director.kt              (Director)
│       ├── MediaType.kt             (ENUM: Movie, TV Show)
│       ├── MediaRating.kt           (ENUM: G, PG, PG-13, R, etc.)
│       ├── MediaGenere.kt           (ENUM: Drama, Action, Comedy, etc.)
│       └── TemaVentana.kt           (Theme enum)
│
├── build.gradle.kts                 (Build config + dependencies)
├── gradle/                          (Gradle wrapper)
├── README.md                        (Original docs)
└── files/                           (Output folder for exports)
```

## 🚀 Quick Start

### Prerequisites
- **JDK 20+**
- **Gradle 8.0+**
- **Kotlin 1.9.21+**

### Setup & Run

```bash
# Clone repository
git clone https://github.com/KilianCuadrado/netflix-crud-app.git
cd netflix-crud-app

# Build project
./gradlew build

# Run application
./gradlew run
```

### Configuration

#### Switch Database (Local vs Remote)

```kotlin
// In JavaFXView.kt

// Use LOCAL SQLite
val dao: INetflixDao = NetflixDao(conLocal)

// Use REMOTE PostgreSQL (Supabase)
val dao: INetflixDao = NetflixDao(conRemot)
```

#### Configure Supabase Credentials

Create `secrets.properties`:
```properties
supabase.url=postgresql://localhost:5432/netflix
supabase.user=your_username
supabase.pass=your_password
supabase.port=5432
```

Gradle automatically injects these into the application.

## 🎮 How to Use

### Main Menu
```
┌─────────────────────────────────┐
│     ¿Qué deseas hacer?          │
├─────────────────────────────────┤
│ [Ver todos los datos]           │
│ [Ver ordenados por columna]     │
│ [Buscar datos por columna]      │
│ [Añadir datos]                  │
│ [Editar datos]                  │
│ [Eliminar datos]                │
│ [Eliminar todos los datos]      │
│ [Exportar datos]                │
│ [Importar datos TXT/CSV]        │
│ [Importar datos BIN]            │
│ [Extra: contar pelis/series]    │
│ [Extra: top 5 países]           │
│ [Extra: año con más estrenos]   │
│ [Extra: género más frecuente]   │
└─────────────────────────────────┘
```

### Example Operations

#### View All Data
```
Retrieves complete Netflix catalog with:
- ID, Type, Title, Director, Cast
- Country, Release Year, Rating, Duration
- Genre, Description, Format
```

#### Search by Title
```
1. Click "Buscar datos..."
2. Enter search term
3. Results display in output area
```

#### Add New Movie
```
1. Click "Añadir datos"
2. Input all fields via dialogs
3. Movie inserted into database
```

#### Export to CSV
```
1. Click "Exportar datos"
2. Select format (CSV/TXT/BIN)
3. File saved to files/ folder
```

## 💻 Architecture Deep Dive

### MVC Pattern

**Model** - Netflix data entities
```kotlin
abstract class Media {
  - id, type, title, director, cast
  - country, releaseYear, rating, duration
  - genres, description, durationValue
  
  methods:
  - toCSV(): String        // Serialization
  - toString(): String     // Formatting
}

class Pelicula : Media     // Movie
class Series : Media       // TV Series
```

**View** - JavaFX GUI
```kotlin
class JavaFXView : Application {
  - Create buttons, dialogs, text areas
  - Bind events to controller actions
  - Update UI with results
  - Support theme switching
}
```

**Controller** - Business Logic
```kotlin
class NetflixController(val dao: INetflixDao) {
  - Coordinates Model & View
  - Executes CRUD operations
  - Formats output for display
  - Handles data validation
}
```

### DAO Pattern (Data Access Object)

**Interface**
```kotlin
interface INetflixDao {
  fun getAllMedia(): MutableList<Media>
  fun getMediaById(id: String): Media?
  fun insertMedia(media: Media)
  fun updateMedia(media: Media): Boolean
  fun deleteMedia(media: Media)
  fun deleteAllMedia()
}
```

**Implementation**
```kotlin
class NetflixDao(val connection: Connection) : INetflixDao {
  // SQL queries abstracted from views/controllers
  // Supports both SQLite and PostgreSQL
  // Automatic connection management
}
```

### Database Abstraction

Swappable database connections:
```kotlin
val conLocal: Connection = DatabaseManager.connection      // SQLite
val conRemot: Connection = SupabaseManager.connection      // PostgreSQL

val dao: INetflixDao = NetflixDao(conLocal)  // Choose which DB
```

## 🔍 Key Concepts Demonstrated

### 1. **Object-Oriented Design**
- Abstract classes (Media)
- Class hierarchies (Pelicula, Series)
- Enumerations (MediaType, MediaRating, MediaGenere)
- Encapsulation with getters

### 2. **Design Patterns**
- **MVC** - Separation of concerns
- **DAO** - Database abstraction
- **Strategy** - Database selection
- **Factory** - Database connection creation

### 3. **Database Operations**
- SQL queries (SELECT, INSERT, UPDATE, DELETE)
- WHERE clauses and filtering
- ORDER BY sorting
- JOIN operations (directors, cast, genres)
- Transaction management

### 4. **Collections & Data Structures**
- MutableList for dynamic data
- Filtering and mapping
- String joining/parsing
- Serialization to CSV/Binary

### 5. **JavaFX GUI Programming**
- Stage, Scene, Layout managers
- Controls (Button, TextArea, TextInputDialog)
- Event handlers (Action events)
- CSS styling
- Theme switching

### 6. **File I/O**
- CSV import/export
- Text file handling
- Binary serialization
- Directory operations

## 📊 Database Schema

```sql
CREATE TABLE media (
  id TEXT PRIMARY KEY,
  type TEXT,           -- 'Movie' or 'TV Show'
  title TEXT,
  director TEXT,       -- comma-separated names
  cast TEXT,           -- comma-separated actors
  country TEXT,
  date_added TEXT,
  release_year INT,
  rating TEXT,         -- G, PG, PG-13, R, NC-17, etc.
  duration TEXT,       -- "90 min" or "2 Seasons"
  genres TEXT,         -- comma-separated genres
  description TEXT,
  duration_value INT   -- numeric value (minutes or seasons)
);
```

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ Full CRUD implementation
- ✅ MVC architectural pattern
- ✅ DAO design pattern
- ✅ Object-oriented programming (inheritance, polymorphism)
- ✅ Database abstraction & connection handling
- ✅ SQLite and PostgreSQL integration
- ✅ GUI development with JavaFX
- ✅ Data validation and error handling
- ✅ File I/O (CSV, TXT, Binary)
- ✅ Data filtering and sorting
- ✅ Gradle build configuration
- ✅ Complex data modeling

## 🔧 Configuration & Customization

### Change Display Fields

Edit `JavaFXView.kt` display methods to add/remove columns.

### Add New Search Criteria

In `NetflixDao.kt`, add new query methods:
```kotlin
fun searchByYear(year: Int): List<Media> {
  return connection.createStatement()
    .executeQuery("SELECT * FROM media WHERE release_year = $year")
    .use { /* parse results */ }
}
```

### Modify Export Format

In `Media.toCSV()`:
```kotlin
fun toCSV(): String {
  // Add/remove fields
  // Change delimiter
  // Format values differently
}
```

## 📈 Future Enhancements

- [ ] User ratings and watchlist
- [ ] Advanced filtering (date range, multiple genres)
- [ ] Data visualization (charts, statistics)
- [ ] Full-text search
- [ ] User authentication
- [ ] REST API wrapper
- [ ] Mobile app version
- [ ] Caching layer
- [ ] Database migration scripts
- [ ] Unit tests
- [ ] Performance optimization (pagination)

## 🐛 Current Limitations

- ⚠️ Single-threaded (GUI blocks during DB operations)
- ⚠️ No pagination (all data loaded in memory)
- ⚠️ No data validation on user input
- ⚠️ Limited error messages
- ⚠️ No undo/redo functionality
- ⚠️ Export files hardcoded to `files/` directory

## 🤝 Contributing

This is a learning project. Feel free to fork, enhance, and submit improvements!

## 📝 Dataset

Original dataset: [Netflix Titles (Cleaned)](https://www.kaggle.com/datasets/youssefkhaled117/netflix-titles-cleaned)
- 5000+ titles
- Movies and TV Series
- Directors, cast, genres
- Release years 1920-2021
- Various content ratings

## 📄 License

Open source - MIT License

---

## 📚 References

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [JavaFX Guide](https://gluonhq.com/products/javafx/)
- [Design Patterns in Kotlin](https://www.baeldung.com/kotlin/design-patterns)
- [DAO Pattern](https://www.baeldung.com/java-dao-pattern)
- [MVC Architecture](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)

---

**DAW Academic Project - Database Access & GUI Development**  
*Status: Functional MVP with advanced features* ✨
