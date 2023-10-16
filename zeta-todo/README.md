## my (quat's) plans

1. Leave AutoRegLib's external API surface basically the same, but start backing its implementation via Zeta.
2. Some parts in ARL assume a "current" modid. If Zeta does away with that restriction, ARL's implementation will be hardcoded for the `quark` modid.
3. WHen AutoRegLib is simply a hollow passthrough library, one big commit that inlines everything.

## How does RegistryHelper work anyway

RegistryHelper is a singleton, everything is static.

* A mapping from Object -> ResourceLocation, for querying registry names before the object is actually registered
* A mapping from modid -> ModData, which holdsa list of all objects to register

You don't explicity provide a mod ID, the "current" modid is read out from Forge.  
-> I think this could be fixed by making everything non-static and requiring you to provide a modid when you create the RegistryHelper - so, closer to Forge's DeferredRegistry system

## Sidedness

There will probably need to be more attention paid to client/server-ness (ex RenderLayerHandler). Fabric does not do any side-stripping & there isn't a common set of `@OnlyIn` annotations to agree on anyways. 

### random code-quality notes (absolutely not major priorities):

* Unused imports
* `VanillaPacketDispatcher` is probably redundant due to `level.sendBlockUpdated`