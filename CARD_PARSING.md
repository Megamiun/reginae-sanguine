# Card Parsing Guide - Reginae Sanguine

This document provides guidance for parsing Queen's Blood card images into structured JSON data for the Reginae Sanguine game engine.

## Card Image Structure

### Visual Elements
- **Card Name**: Large text at bottom of card
- **Power**: Number inside circle on top-right corner
- **Rank**: Number of pawn icons on top-left corner
- **Grid Pattern**: 5x5 grid showing increment positions and effect areas
- **Effect Text**: Description below card name
- **Card ID**: Small text at bottom-left corner (e.g., "#001")

### Grid Reading
- **Yellow squares**: Increment positions (where ranks are raised by 1)
- **Red squares**: Effect displacement positions
- **White squares**: Where the card is placed, at position (x=0, y=0)

## JSON Structure
- Should follow Card object structure as much as possible
- Positions should always be an { x, y } object
- For the moment, should have a field gridParsed: false
- Don't export empty or null fields
- Should add field 'filename' with the simple name of the file

## Effects special considerations
### General
- When numerical, should fill field 'amount'
- When referring to another card, should fil id on list field 'cardIds'

## Effects
- `RaisePower`: Modifies power of cards (amount: positive/negative integer, target: ALLIES/ENEMIES/ANY/SELF)
- `RaiseRank`: Modifies rank increment amount for positions (amount: positive integer)
- `AddCardsToHand`: Adds specific card to player's hand (cardIds: related card IDs)
- `DestroyCards`: Destroys cards on affected tiles
- `ReplaceAlly`: Replaces an allied card with optional power modification (powerMultiplier: 0=no modification, 1=raise by replaced power, -1=lower by replaced power; target: ALLIES/ANY for power modification target)
- `StatusBonus`: Power modification based on card state (enhancedAmount/enfeebledAmount: integers)
- `ScoreBonus`: Provides score bonus (amount: positive integer)
- `LoserScoreBonus`: Provides score bonus equal to loser's score (no additional fields)
- `SpawnCards`: Spawns specific cards in empty positions (cardIds: array of card IDs to spawn depending on rank)
- `FlavourText`: No real effect (description only)

## Trigger Types
Triggers are objects with a `type` field and optional condition fields:

- `WhenPlayed`: Effect activates when card is played (scope: ALLIES/ENEMIES/ANY - for counting triggers, defaults to SELF)
- `WhenDestroyed`: Effect activates when card is destroyed (scope: ALLIES/ENEMIES/ANY - for counting triggers, defaults to SELF)
- `WhenFirstStatusChanged`: Effect activates when card status first changes (status: ENHANCED/ENFEEBLED)
- `WhenFirstReachesPower`: Effect activates when card's power first reaches threshold (threshold: integer)
- `WhileActive`: Effect is active while card remains on board
- `WhenLaneWon`: Effect activates when the player wins the lane
- `OnStatusChange`: Effect based on count of cards with specific status (status: ENHANCED/ENFEEBLED/ANY, scope: ALLIES/ENEMIES/ANY - both default to ANY)
- `OnRoundEnd`: Effect activates at the end of each round
- `None`: No trigger (used for FlavourText effects)

## Status Types (Enum Values)
- `ENHANCED`: Card is in enhanced state
- `ENFEEBLED`: Card is in enfeebled state
- `ANY`: Cards is in any affected state

## Target Types (Enum Values)
- `ALLIES`: Effect only applies to allied cards
- `ENEMIES`: Effect only applies to enemy cards
- `ANY`: Effect applies to both allied and enemy cards
- `SELF`: Effect applies to the card itself or its player

## Parsing Rules
### Card Identification
- Blue and Red are always equivalent. Only parse blue cards.

### Power and Rank Reading
- **Power**: Read number inside circle (top-right)
- **Rank**: Count pawn icons (top-left)

### Effect Parsing
- Extract effect description from card text
- Identify trigger words: "When played", "While active", "When destroyed", etc
- Identify target words: "allied", "enemy", "all"
- Map to appropriate effect types and parameters
- When another card is referred, ignore it on a first pass

### Grid Parsing
- Set `gridParsed: false` initially
- Ignore parsing for the moment
- Yellow squares become increment positions
- Red squares become effect displacement positions
- Requires manual verification due to visual complexity

## Data Validation
- Ensure all required fields are present
- Remove empty arrays/objects to keep JSON clean
- Verify effect types match implemented Effect classes
- Cross-reference card IDs with available image files

## Implementation Notes
- Card model uses `Set<Position>` for increments
- Effects use specific classes like `RaiseRank`, `RaisePower`
- JSON structure should map cleanly to Kotlin data classes
- Consider future extensibility when adding new effect types
- Every time a new enumeration(trigger/target/effect) is added or modified, Claude should update this file

## Second Pass
- After all files processed, should link SpawnCards and AddCardsToHand card ids