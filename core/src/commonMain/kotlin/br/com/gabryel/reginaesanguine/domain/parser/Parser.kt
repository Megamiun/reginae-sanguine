package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHand
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.FlavourText
import br.com.gabryel.reginaesanguine.domain.effect.type.LoserScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerByCount
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRank
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAlly
import br.com.gabryel.reginaesanguine.domain.effect.type.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCards
import br.com.gabryel.reginaesanguine.domain.effect.type.StatusBonus
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
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
    extraTriggers: Set<KClass<Trigger>> = emptySet(),
    configure: SerializersModuleBuilder.() -> Unit = {}
) = Json {
    ignoreUnknownKeys = true
    explicitNulls = false

    serializersModule = SerializersModule {
        polymorphic(Effect::class) {
            subclass(RaisePower::class)
            subclass(RaisePowerByCount::class)
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

        configure()
    }
}
