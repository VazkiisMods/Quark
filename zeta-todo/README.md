## How does RegistryHelper work anyway

RegistryHelper is a singleton, everything is static.

* A mapping from Object -> ResourceLocation, for querying registry names before the object is actually registered
* A mapping from modid -> ModData, which holdsa list of all objects to register

You don't explicity provide a mod ID, the "current" modid is read out from Forge.  
-> I think this could be fixed by making everything non-static and requiring you to provide a modid when you create the RegistryHelper - so, closer to Forge's DeferredRegistry system

### random code-quality notes (absolutely not major priorities):

* Unused imports
* `VanillaPacketDispatcher` is probably redundant due to `level.sendBlockUpdated`