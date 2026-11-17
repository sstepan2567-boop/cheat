# cheat

Fabric client mod for Minecraft Java Edition **1.21.4** (Yarn `1.21.4+build.8`).

The project is a client-side bypass layer: a module system, a mixin-driven packet layer,
server state synchronisation (teleport transactions, block-action sequences, tick rate),
movement helpers and per-anticheat bypass modes plus a disabler layer.

## Stack

| Component | Version |
| --- | --- |
| Minecraft | 1.21.4 |
| Java | 21 |
| Fabric Loader | 0.20.1 |
| Fabric Loom | 1.19.4 |
| Yarn mappings | 1.21.4+build.8 |
| Fabric API | 0.119.4+1.21.4 |

## Layout

```
src/main/java/com/cheat
├── CheatClient.java          entry point, event bus / render hook wiring
├── bypass/                   per-anticheat modes (grim, vulcan, verus, matrix, ncp, spartan, intave, hypixel)
├── disabler/                 Disabler module + per-anticheat profiles
├── event/                    Event, EventHandler, EventBus, PacketEvent, TickEvent
├── mixin/                    MinecraftClientMixin, packet mixins, interaction manager mixin
├── module/                   Module, ModuleCategory, ModuleManager
├── movement/                 CollideReset, MicroJump, PacketQueueModule, PingPongModule, PlayerMoveRewriter
├── packet/                   PacketLayer (outgoing rewrite / raw send bridge)
├── state/                    StateSync (teleport ids, sequences, tick rate)
├── util/                     Math, timer, player and combat helpers
└── visual/                   ESP, Tracers, Nametags
```

## Build

```
./gradlew build
```

The build downloads Gradle, Minecraft, Yarn and Fabric API from `services.gradle.org` and
`maven.fabricmc.net`; those hosts must be reachable.

## Notes

* `PlayerMoveC2SPacket` is immutable, so NoFall / scaffold modes cancel the original packet
  and send a rewritten copy through `PacketLayer.sendRaw(...)`.
* Transaction ids in 1.21.4 are the `PlayerPositionLookS2CPacket.teleportId()` /
  `TeleportConfirmC2SPacket` pair (the old `TransactionS2CPacket` no longer exists); block
  action acks are `PlayerActionResponseS2CPacket.sequence()`.
