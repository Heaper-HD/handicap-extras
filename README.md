# Handicap Extras

An addon for the [Handicap](https://github.com/FERSPl/Handicap) mod that adds new handicaps, focused on multiplayer chaos.

The new handicaps plug straight into Handicap's own systems: they show up in its commands, random rolls, timer, settings and handicap list, and are saved and synced like the originals.

## Handicaps

| Handicap | Effect                                                                                                     |
|---|------------------------------------------------------------------------------------------------------------|
| Separation Anxiety | With no other player, tamed pet of yours or villager nearby, you slow down and your view starts trembling. |

## Requirements

- Minecraft 26.3
- Fabric Loader and Fabric API
- [Handicap](https://www.curseforge.com/minecraft/mc-mods/handicap) 1.6.0 (Fabric)
- Java 25

Install on **both the server and every client**. The new handicaps are added to Handicap's handicap list, so a client without the addon would disagree with the server about which handicap is which.

## Usage

Everything is controlled through Handicap's existing commands and settings screen. The new handicaps appear there alongside the original ones and can be given, removed, disabled and rolled the same way.

## Building

1. Put the Handicap Fabric jar in `libs/` (the build expects `libs/handicap-fabric-1.6.0.jar`).
2. Run `./gradlew build`. The addon jar ends up in `build/libs/`.

### Testing multiplayer locally

- `runServer` creates `run/`. Set `eula=true` in `run/eula.txt`, and `online-mode=false` and `enforce-secure-profile=false` in `run/server.properties`.
- The build generates extra client run configs (`Minecraft Client 2`, `3`, ...) with their own usernames and run folders. The count is set by `dev_clients` in `gradle.properties`.
- Connect each client to `localhost`.

## Credits

Built on top of [Handicap](https://github.com/FERSPl/Handicap) by FERSPI (LGPL-3.0). This addon does not include or modify Handicap's code; it extends it at runtime.

## License

See [LICENSE](LICENSE).
