package br.com.gabryel.reginaesanguine.domain.parser

import br.com.gabryel.reginaesanguine.domain.effect.type.AddCardsToHandDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCardsDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.Effect
import br.com.gabryel.reginaesanguine.domain.effect.type.FlavourText
import br.com.gabryel.reginaesanguine.domain.effect.type.NoEffect
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseLaneIfWon
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerByCount
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePowerOnStatus
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRankDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseWinnerLanesByLoserScore
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.effect.type.SpawnCardsPerRank
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
 * @return A Json Parser
 */
fun gameJsonParser(
    extraEffects: Set<KClass<Effect>> = emptySet(),
    configure: SerializersModuleBuilder.() -> Unit = {}
) = Json {
    ignoreUnknownKeys = true
    explicitNulls = false

    serializersModule = SerializersModule {
        polymorphic(Effect::class) {
            subclass(AddCardsToHandDefault::class)

            subclass(DestroyCardsDefault::class)

            subclass(RaisePower::class)
            subclass(RaisePowerByCount::class)
            subclass(RaisePowerOnStatus::class)

            subclass(RaiseRankDefault::class)

            subclass(RaiseLaneIfWon::class)
            subclass(RaiseWinnerLanesByLoserScore::class)

            subclass(ReplaceAllyDefault::class)
            subclass(ReplaceAllyRaise::class)

            subclass(SpawnCardsPerRank::class)

            subclass(FlavourText::class)
            subclass(NoEffect::class)

            extraEffects.forEach(::subclass)
        }

        configure()
    }
}
