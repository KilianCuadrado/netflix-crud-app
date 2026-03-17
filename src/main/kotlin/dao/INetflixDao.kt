package dao

import model.Media
import model.Pelicula
import model.Series

interface INetflixDao {
    public fun getAllMedia(): MutableList<Media>
    public fun getAllMediaOrdered(column: String, ascending: Boolean = true): MutableList<Media>
    public fun getMediaById(id: String): Media?
    public fun getAllSeries(): List<Series>
    public fun getAllMovies(): List<Pelicula>
    public fun deleteAllMedia()
    public fun insertMedia(media: Media)
    public fun updateMedia(media: Media): Boolean
    public fun deleteMedia(media: Media)
}