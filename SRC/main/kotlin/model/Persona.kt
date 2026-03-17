package model

import java.io.Serializable

/**
 * Classe abstracta que representa una persona, que pot ser actor o director.
 * Conté la propietat del nom i el constructor per inicialitzar-la.
 * @author KilianCuadrado
 * @version 1.0
 * @since 2026-03-11
 * @see Actor
 * @see Director
 */

open abstract class Persona(name: String) : Serializable {

    private var name: String = name

    fun getName(): String{
        return this.name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Persona
        return name.equals(other.name, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return name.lowercase().hashCode() * 31 + this::class.java.hashCode()
    }

    override fun toString(): String {
        return name
    }

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
