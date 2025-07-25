package br.com.gabryel.reginaesanguine.domain

/**
 * Entity Checker Types:
 * - Enemy
 * - Ally
 * - Self
 * - Any
 *
 * Known effect types:
 * - Raise Entity Power by X
 * - Raise Empty Position rank by X
 * - Add card X to hand
 * - Spawn card X
 * - Gives X Score bonus Points
 * - Replace Entity
 * - Destroy Entities
 *
 * Known multipliers:
 * - Number of Other Enhanced Entity Cards
 * - Number of Other Enfeebled Entity Cards
 * - Replaced Entity Power
 *
 * Known conditions:
 * - When Entity Played
 * - When Entity first enhanced
 * - When Entity first enfeebled
 * - When Entity Destroyed
 * - When Entity Played by hand
 * - When Self Power First Reaches X
 * - When Lane Won
 * - While Self Active
 * - While Self Enhanced
 * - While Self Enfeebled
 * - Permanent
 * - Every Action
 *
 * Positional types
 * - Adjacent
 * - Affected Tiles(Check what this means)
 *
 * * X can be negative for inverse effect
 */
interface Effect

class RaisePower(val amount: Int = 1) : Effect

class WinLaneBonusPoints(val bonusPoints: Int = 1) : Effect

class DestroyEntity : Effect
