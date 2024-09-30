# Roadworks

Roadworks add immersive, American-style traffic management features to Minecraft. **It is currently in beta, so please expect some bugs!** Currently, the mod adds markings, cones, signals, bollards, and signs, though more may come in the future.

## Todo
- Add an included resource pack to add European-style signage
- More signs
- More markings
- Pedestrian crossing signals
- Make CC an optional dependency

## Installation Requirements
- Minecraft 1.20.1
- Fabric Loader >+ 0.14.22
- Fabric API >= 1.87.0
- Fabric Language Kotlin >= 1.10.8
- CC: Tweaked >= 1.107.0

## Features
### Signals
Controllable via a Traffic Controller and can be connected to one with a linker. Signals connected to a traffic controller can be controlled via a ComputerCraft computer, wrapped as a peripheral.

### Posts
Posts connect when placed next to each other. They can connect to signals and signs.

### Signs
Signs can be placed onto posts, or onto walls. Many are currently included, but there will hopefully be even more in the future.

### Markings
Most that extend over a whole block will connect to a filler is placed in the correct way. Hopefully it's pretty easy to understand.

### Bollards
Pretty simple. Kinda neat.

## CC API
This will be improved in the future and moved to a wiki page.

### `getSignalType(id: int): string?`
Gets the type of signal ID. Will be one of:
- `three_head`
- `three_head_left`
- `three_head_right`
- `three_head_straight`
- `five_head_left`
- `five_head_right`

### `getSignals(): table`
Gets all signals connected to this controller. This returns an array of dictionaries containing the signal ID and the signal's type.

### `getSignalsOfType(type: string): table`
Returns an array of IDs which have the specified type. See `getSignalType` for available types.

### `hasId(id: int): boolean`
Returns true if the traffic cabinet has the specified id.

### `hasLight(type: string, light: string): boolean`
Returns true if the specified type has the specified light.

### `setThreeHead(id: int, red: boolean, yellow: boolean: green: boolean): boolean`
Sets the specified three-head signal of ID to the specified values.

### `setFiveHead(id: int, red: boolean, yellowLeft: boolean: greenLeft: boolean, yellowRight: boolean, greenRight: boolean): boolean`
Sets the specified five-head signal of ID to the specified values.

## Contribute
If you find a bug or have a feature request, please make an issue. PR's, as always, are accepted and appreciated. PR's will not be accepted if they produce bugs or add additional dependencies.
