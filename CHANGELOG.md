# Changelog

All notable changes to this project are documented here.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Integration with Handicap 1.6.0: new handicaps are added to Handicap's handicap list, so they work with its commands, random rolls, timer, settings, saving and syncing.
- Server and client handicap framework with give/remove/tick lifecycle, cleanup on respawn and disconnect, and separate registries for each side.
- Shared camera shake system for client-side handicaps.
- New handicaps:
  - **Separation Anxiety**: being away from other players and your own tamed pets slows you down and makes your view tremble.
  - **Gigantism**: you're 50% bigger, with more reach and step height, but you don't fit through normal doors.
  - **Motion Sickness**: riding anything makes you nauseous and weak.
  - **Agoraphobia**: under the open sky you slow down and tremble.
  - **Nyctophobia**: in the dark without a light in hand, you're slower and weaker.
  - **Sun Allergy**: direct sunlight sets you on fire unless you wear a helmet.
  - **Acrophobia**: near a big drop you slow down and tremble.
  - **Aquaphobia**: in water you swim slower, hit weaker and panic.
  - **Hyperphagia**: you get hungry much faster.
  - **Photophobia**: bright light without a helmet slows and weakens you, and you squint.
  - **Pyrophobia**: near fire, lava, lit furnaces or campfires you panic.

### Changed
- **Arthritis" (from Handicap): below full health you also can only eat and you deal 60% less damage.