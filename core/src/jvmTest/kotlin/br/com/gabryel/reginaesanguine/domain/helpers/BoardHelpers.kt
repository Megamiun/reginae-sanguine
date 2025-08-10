package br.com.gabryel.reginaesanguine.domain.helpers

import br.com.gabryel.reginaesanguine.domain.atColumn

// TODO considers the board atColumn be 5x3
val LEFT_COLUMN = 0
val CENTER_LEFT_COLUMN = 1
val CENTER_COLUMN = 2
val CENTER_RIGHT_COLUMN = 3
val RIGHT_COLUMN = 4

val TOP_LANE = 2
val MIDDLE_LANE = 1
val BOTTOM_LANE = 0

val A1 = TOP_LANE atColumn LEFT_COLUMN
val A2 = TOP_LANE atColumn CENTER_LEFT_COLUMN
val A3 = TOP_LANE atColumn CENTER_COLUMN
val A4 = TOP_LANE atColumn CENTER_RIGHT_COLUMN
val A5 = TOP_LANE atColumn RIGHT_COLUMN

val B1 = MIDDLE_LANE atColumn LEFT_COLUMN
val B2 = MIDDLE_LANE atColumn CENTER_LEFT_COLUMN
val B3 = MIDDLE_LANE atColumn CENTER_COLUMN
val B4 = MIDDLE_LANE atColumn CENTER_RIGHT_COLUMN
val B5 = MIDDLE_LANE atColumn RIGHT_COLUMN

val C1 = BOTTOM_LANE atColumn LEFT_COLUMN
val C2 = BOTTOM_LANE atColumn CENTER_LEFT_COLUMN
val C3 = BOTTOM_LANE atColumn CENTER_COLUMN
val C4 = BOTTOM_LANE atColumn CENTER_RIGHT_COLUMN
val C5 = BOTTOM_LANE atColumn RIGHT_COLUMN
