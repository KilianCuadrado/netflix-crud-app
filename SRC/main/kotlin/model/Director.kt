package model

import controller.NetflixController
import dao.NetflixDao
import view.netflixController

/**
 * Classe que representa un director, que és un tipus de persona.
 * Conté una llista de continguts dirigits pel director.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Persona
 * @see Media
 */

class Director: Persona {

    private var directed: MutableList<Media>

    constructor(name: String): super(name){

        // Evita recursió quan el DAO construeix objectes Director en llegir la BD.
        this.directed = mutableListOf()

    }

    /**
     * Obté totes les pel·lícules i sèries dirigides per aquest director.
     * @return llista de media dirigits per aquest director.
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun directedMedia(controller: NetflixDao): MutableList<Media>{
        val directedMedia: MutableList<Media> = mutableListOf()

        controller.getAllMedia().forEach { media ->

            if(media.getDirector().contains(this)){

                directedMedia.add(media)

            }
        }

        return directedMedia
    }

    /**
     * Retorna la propietat interna `directed`.
     * @return llista interna de media dirigits associada al director.
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun getDirected(): MutableList<Media>{
        return this.directed
    }

}