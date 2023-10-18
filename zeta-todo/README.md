# New concepts in Zeta's module system

The first module ported to `@ZetaLoadModule` is `DoubleDoorOpeningModule` if you'd like a worked example.

* **Zeta's event bus**.
  * Lifecycle events (loading, construction, registry events, etc) are managed by subscribing to Zeta events, instead of by overriding specific methods in QuarkModule.
  * Gameplay events (mouse clicks, interacting, etc) are also managed by subscribing to Zeta events, instead of using the Forge event bus.
  * There are two event busses - a "load bus" for lifecycle events, and a "play bus" for gameplay events.
    * This loosely corresponds to the fml mod bus and MinecraftForge.EVENT_BUS, but the more important distinction is that *all* modules receive load events but only *enabled* modules receive play events.
  * Zeta modules are automatically subscribed to both busses. Mark event handlers with `@LoadEvent` and `@PlayEvent`.
* **Client-only modules**.
  * If a `@ZetaLoadModule` is annotated with `side = ModuleSide.CLIENT_ONLY`, it will not load at *all* on the server. It's like it's not even there.
  * Yes this causes problems when you open the client and server at the same time on your computer, they disagree on what the config should be. Arguably client-only options shouldn't even be *in* the common config, but that will come later.
  * This *replaces* Quark's `hasSubscriptions`/`subscribeOn` system.
* **Client module replacements**.
  * On the client, if a class is annotated with `@ZetaLoadModule(clientReplacement = true)`, and the class extends another `ZetaModule`, the replacement module will load *instead* of its superclass.
  * Since Fabric doesn't do side-stripping (the removal of things flagged `@OnlyIn(Dist.CLIENT)`), a client replacement module is the best place to put client-only code.
  * Yeah it's hard to explain. See `DoubleDoorOpeningModule`. On the server `DoubleDoorOpeningModule` loads, and on the client `DoubleDoorOpeningModule$Client` loads.
* **General notes**.
  * It is recommended to make fewer things `static`. It's easy to obtain module instances with `Quark.ZETA.modules.get`.
  * Some things (most importantly, anything and everything relating to config) are unchanged for now. In due time.

In the interim period `QuarkModule` extends `ZetaModule`, and some of the remaining features (subscribing to `MinecraftForge.EVENT_BUS`, method-based lifecycle events) are implemented in terms of ZetaModule or in scattered `instanceof QuarkModule` blocks. This is just to keep the rest of the mod on life support and it *is* imperfect - many QuarkModules are broken on the dedicated server (it's not safe to subscribe *at all* to *any* annotation-based event bus - even Zeta's - if you have even one `@OnlyIn` method, so Zeta can't subscribe these modules to the *load* event bus either, so they don't receive lifecycle events or register blocks...)

So tl;dr for zeta-fying a module:

* swap LoadModule to ZetaLoadModule, remove hasSubscriptions/subscribeOn, and if the module is truly client-only add `side = ModuleSide.CLIENT_ONLY`,
* change the superclass from `QuarkModule` to `ZetaModule`,
* change `@Override pubilc void setup()` to `@LoadEvent public void setup(ZCommonSetup event)`,
* move everything marked `@OnlyIn(Dist.CLIENT)` (and all `@SubscribeEvent`s if subscribeOn was formerly `Dist.CLIENT` only) into a client module replacement,
* create cross-platform versions of any missing `@SubscribeEvent`s in Zeta and subscribe to them with `@PlayEvent`,
* remove all other mentions of Forge and add an appropriate indirection layer in Zeta.

Repeat 160x.

# The initial Zeta sketch

The entrypoint, `Zeta`, is an abstract class that must be implemented per-loader. Initialize a `Zeta` then stick it somewhere global.

## Zeta Modules

A "module" in Zeta is a logical grouping of blocks, items, event handlers, and other content. Modules can be enabled and disabled.

The module loading process:

* `Zeta#modules.load` accepts a `ModuleFinder`, which returns a `Stream<ZetaLoadModuleAnnotationData>`
  * `ModuleFinder` can be implemented however you like. You can try `ServiceLoaderModuleFinder` (stock Java), `ModFileScanDataModuleFinder` (Forge), or returning a hardcoded list. 
  * Each must correspond to a class that extends `ZetaModule` *and* is annotated with `ZetaLoadModule`.
* `load` then filters the tentative modules to only those that will actually be loaded
  * eg. client-only modules will be skipped (*completely*) on the server
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

Unlike Forge, event bus subscribers are typechecked, so you will actually get an error instead of it silently failing!!!!!!!11111

All Zeta Modules are automatically subscribed (statically and non-statically) to the `loadBus`. Enabled modules are subscribed to the `playBus`.

### Firing events

Call `bus.fire()`. Note that if the `@FireAs` annotation is applied to something fired over the event bus, it will actually look up the listener list corresponding to the type *inside the annotation*, rather than itself:

```java
//if you have this setup
class Supertype implements IZetaLoadEvent {}
@FireAs(Supertype.class) class Subtype extends Supertype {}

//and you subscribe to the supertype
@LoadEvent
public void blah(Supertype e) {}

//you can fire with the subtype, and it'll trigger the event listener
bus.fire(new Subtype());
```

This is a bit unusual, but allows for events to have a split API and implementation.

If an event implements `Cancellable`, calling `cancel` will stop its propagation to the rest of the event listeners.

## Zeta Network

Just the netcode that was already in Quark/ARL tbh

## Zeta Registry

its literally autoreglib

# code goals

* Cut down on `static` usage
* Keep the components of Zeta relatively loosely coupled if at all possible
  * I'm still deciding how many fields should go in `Zeta`. Like does the module system belong there (probably). Does the registry belong there (maybe?)

## figuring out how quark hints work

Hints pipeline

* `HintManager.loadHints` is called from uhh, somewhere deep in config code
* It iterates over all `@Hint` fields and adds them to `module.hints`
* `ModuleLoader.INSTANCE.addStackInfo` is called from JEI integration, calls `addStackInfo` on all enabled quark modules
* QuarkModule `addStackInfo` then appends hints from `module.hints` (which is the only usage of this field)

Some things that might make this easier?

* Modules could be in charge of loading their own hints. This'd let `module.hints` be a private field
* It could be moved from "module step that only looks at enabled modules" to an event on Zeta's play bus

## figuring out how quark module discovery works

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