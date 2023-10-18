# The initial Zeta sketch

The entrypoint, `Zeta`, is an abstract class that must be implemented per-loader. Initialize a `Zeta` then stick it somewhere global.

## Zeta Modules

A "module" in Zeta is a logical grouping of blocks, items, event handlers, and other content. Modules can be enabled and disabled.

The module loading process:

* `Zeta#modules.load` accepts a `ModuleFinder`, which returns a `Stream<TentativeModule>`
  * `ModuleFinder` can be implemented however you like. You can try `ServiceLoaderModuleFinder` (stock Java), `ModFileScanDataModuleFinder` (Forge), or returning a hardcoded list. 
  * Each `TentativeModule` corresponds to a class that extends `ZetaModule` *and* is annotated with `ZetaLoadModule`.
* `load` then filters the tentative modules to only those that will actually be loaded.
  * Stuff about "client only modules" that isn't really finished yet, but yknow, stuff like "client module will not be loaded on server"
* Each module is constructed, initialized with information from the annotation, and subscribed to the Zeta Events busses
* `postConstruct` is called on the module (TODO i don't think we really need this lol)

## Zeta Events

### Busses

A `Zeta` comes with two event busses - the `loadBus` is for initial game-startup stuff, and `playBus` is for in-game events.

Each event bus has an associated annotation (`@LoadEvent` and `@PlayEvent`) and an associated "event root" interface (`IZetaLoadEvent` and `IZetaPlayEvent`).

To add an event handler:

* Write a function that takes one argument (which must be a *direct* descendent of the bus's event root interface).
* Annotate it with the bus's associated annotation.
* Call `subscribe`.
  * If the function is `static`, pass the class owning the function.
  * If the function is non-static, pass an instance of the object you want to subscribe.

Unlike Forge, if you subscribe to the wrong event bus, you will actually get an error instead of it just silently failing!!!!!!!11111

All Zeta Modules are automatically subscribed (statically and non-statically) to the `loadBus`. Enabled modules are subscribed to the `playBus`.

### Firing events

`ZetaEventBus#fire` accepts any object that implements the bus's event root. To match this object to a listener list, the bus first climbs the object's type hierarchy, looking for a superclass or superinterface that *directly* implements the event root. More concretely, if the event root is `I`, and you have `class A implements I`/`class B extends A`, if you `.fire(new B())`, listeners for `A` will be called.

This is a bit unusual, but allows for events to have a split API and implementation.

If an event implements `Cancellable`, calling `cancel` will stop its propagation to the rest of the event listeners.

## Zeta Network

Just the netcode that was already in Quark/ARL tbh

## Zeta Registry

its literally autoreglib

## code goals

* Cut down on `static` usage
* Keep the components of Zeta relatively loosely coupled if at all possible
  * I'm still deciding how many fields should go in `Zeta`. Like does the module system belong there (probably). Does the registry belong there (maybe?)

# my (quat's) plans

- [x] An event bus that allows modules to conveniently subscribe to stuff
- [ ] Lifecycle events, instead of many virutal functions in QuarkModule
- [ ] Some notion of "client module extensions"
- [ ] Kill as much `static` as possible

Probably a good idea to split `TentativeModule` into two parts
* one that handles "reading data out of the annotation" (actual annotation or Forge asm weirdness)
* one that handles logic like "if the module is uncategorized, guess the category from the package name" / "if the module has no name, pick one from the class name"

## Hints

Hints pipeline

* `HintManager.loadHints` is called from uhh, somewhere deep in config code
* It iterates over all `@Hint` fields and adds them to `module.hints`
* `ModuleLoader.INSTANCE.addStackInfo` is called from JEI integration, calls `addStackInfo` on all enabled quark modules
* QuarkModule `addStackInfo` then appends hints from `module.hints` (which is the only usage of this field)

Some things that might make this easier?

* Modules could be in charge of loading their own hints. This'd let `module.hints` be a private field
* It could be moved from "module step that only looks at enabled modules" to an event on Zeta's play bus

## Module discovery

entrypoint: ModuleLoader.start

A forge modloader service finds all `LoadModule` annotations, these end up in `ModuleLoader.foundModules`. `construct` is called on each of them (theyre already constructed actually, `construct` is just a method)

Config stuff happens, it's a maze of global singletons, i dont get it tbh. On the first registry event (big hack to avoid registry freeze nonsense) REGISTER and POST_REGISTER is dispatched, CreativeTabHandler.finalizeTabs is called, more config stuff.

Many Forge lifecycle/loading events are threaded through the dispatching system. Moooost of these are dispatched unconditionally to every module, but there are a few that check `enabled` first. Gameplay events are subscribed to using the regular `@SubscribeEvent`/`MinecraftForge.EVENT_BUS` system, and the `QuarkModule.setEnabledAndManageSubscriptions` function ensures only enabled modules are still subscribed to the bus.

## How does RegistryHelper work anyway

RegistryHelper is a singleton, everything is static.

* A mapping from Object -> ResourceLocation, for querying registry names before the object is actually registered
* A mapping from modid -> ModData, which holdsa list of all objects to register

You don't explicity provide a mod ID, the "current" modid is read out from Forge.  
-> I think this could be fixed by making everything non-static and requiring you to provide a modid when you create the RegistryHelper - so, closer to Forge's DeferredRegistry system

### Handle-based registration system ??

This would be a large overhaul of the registration system (and it would make constructor registration impossible), but given the registry-freezing shenanigans Quark is just *barely* working around I think it might be a good change in the long run.

Right now `register` takes `Object`, but ideally it would take `Supplier<Object>` and return a "handle" type.

The handle provides access to the registered object only after it's been registered, but immediately provides access to the `ResourceLocation` since it's handy.

## Sidedness

There will probably need to be more attention paid to client/server-ness (ex RenderLayerHandler). Fabric does not do any side-stripping.