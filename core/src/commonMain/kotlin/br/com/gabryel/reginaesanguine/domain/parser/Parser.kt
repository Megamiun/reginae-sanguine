package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.effect.AddCardsToHand
import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.Effect
import br.com.gabryel.reginaesanguine.domain.effect.FlavourText
import br.com.gabryel.reginaesanguine.domain.effect.LoserScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.None
import br.com.gabryel.reginaesanguine.domain.effect.OnGameEnd
import br.com.gabryel.reginaesanguine.domain.effect.OnStatusChange
import br.com.gabryel.reginaesanguine.domain.effect.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.RaiseRank
import br.com.gabryel.reginaesanguine.domain.effect.ReplaceAlly
import br.com.gabryel.reginaesanguine.domain.effect.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.SpawnCards
import br.com.gabryel.reginaesanguine.domain.effect.StatusBonus
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenDestroyed
import br.com.gabryel.reginaesanguine.domain.effect.WhenFirstReachesPower
import br.com.gabryel.reginaesanguine.domain.effect.WhenFirstStatusChanged
import br.com.gabryel.reginaesanguine.domain.effect.WhenLaneWon
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.reflect.KClass

/**
 * Generates a parser that can be used for SerDe for the core library.
 *
 * @param extraEffects List of extra effects to add to Serialization. New classes should be annotated with [kotlinx.serialization.Serializable] and [kotlinx.serialization.SerialName]
 * @param extraTriggers List of extra triggers to add to Serialization. New classes should be annotated with [kotlinx.serialization.Serializable] and [kotlinx.serialization.SerialName]
 * @return A Json Parser
 */
fun gameJsonParser(
    extraEffects: Set<KClass<Effect>> = emptySet(),
    extraTriggers: Set<KClass<Trigger>> = emptySet()
) = Json {
    ignoreUnknownKeys = true

    serializersModule = SerializersModule {
        polymorphic(Effect::class) {
            subclass(RaisePower::class)
            subclass(RaiseRank::class)
            subclass(AddCardsToHand::class)
            subclass(SpawnCards::class)
            subclass(ScoreBonus::class)
            subclass(LoserScoreBonus::class)
            subclass(DestroyCards::class)
            subclass(ReplaceAlly::class)
            subclass(StatusBonus::class)
            subclass(FlavourText::class)

            extraEffects.forEach(::subclass)
        }

        polymorphic(Trigger::class) {
            subclass(WhenPlayed::class)
            subclass(WhenDestroyed::class)
            subclass(OnStatusChange::class)
            subclass(WhenFirstStatusChanged::class)
            subclass(WhenFirstReachesPower::class)
            subclass(WhenLaneWon::class)
            subclass(WhileActive::class)
            subclass(OnGameEnd::class)
            subclass(None::class)

            extraTriggers.forEach(::subclass)
        }
    }
}
