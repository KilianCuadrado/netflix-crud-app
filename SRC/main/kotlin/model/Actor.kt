package model

import dao.NetflixDao


/**
 * Classe que representa un actor, que és un tipus de persona.
 * Conté una llista de continguts on apareix l'actor.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Persona
 * @see Media
 */

class Actor: Persona {

    private var appearsIn: MutableList<Media>

    constructor(name: String): super(name){

        this.appearsIn = mutableListOf()

    }

    /**
     * Retorna la llista de continguts on apareix aquest actor.
     * Fa una consulta a totes les dades i filtra les que contenen aquest actor al cast.
     * @return llista de media on apareix l'actor.
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun appearsInMedia(controller: NetflixDao): MutableList<Media>{
        controller.getAllMedia().forEach { media ->
            if(media.getCast().contains(this)){
                this.appearsIn.add(media)
            }
        }
        return this.appearsIn
    }

    /**
     * Retorna la propietat interna amb les aparicions de l'actor.
     * @return llista interna de media associada a l'actor.
     * @author KilianCuadrado
     * @version 1
     * @since 2026-03-14
     */
    fun getAppearsIn(): MutableList<Media>{
        return this.appearsIn
    }
}