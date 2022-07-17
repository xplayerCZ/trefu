package cz.davidkurzica.trefu.model

data class TimetablesFormData(
    val selectedStop: Stop,
    val selectedLine: Line,
    val selectedDirection: Direction,
)