# New concepts in Zeta's module system

The first module ported to `@ZetaLoadModule` is `DoubleDoorOpeningModule` if you'd like a worked example.

* **Zeta's event bus**.
  * Lifecycle events (loading, construction, registry events, etc) are managed by subscribing to Zeta events, instead of by overriding specific methods in QuarkModule.
  * Gameplay events (mouse clicks, interacting, etc) are also managed by subscribing to Zeta events, instead of using the Forge event bus.
  * There are two event busses - a "load bus" for lifecycle events, and a "play bus" for gameplay events.
    * This loosely corresponds to the fml mod bus and MinecraftForge.EVENT_BUS, but the more important distinction is that *all* modules receive load events but only *enabled* modules receive play events.
  * Zeta modules are automatically subscribed to both busses. Mark event handlers with `@LoadEvent` and `@PlayEvent`.
  * Other classes can be subscribed to the event bus with `.subscribe`.
* **Client module replacements**.
  * On the client, if a class is annotated with `@ZetaLoadModule(clientReplacement = true)`, and the class extends another `ZetaModule`, the replacement module will load *instead* of its superclass.
  * Since Fabric doesn't do side-stripping (the removal of things flagged `@OnlyIn(Dist.CLIENT)`), a client replacement module is the best place to put client-only code.
  * Yeah it's hard to explain. See `DoubleDoorOpeningModule`. On the server `DoubleDoorOpeningModule` loads, and on the client `DoubleDoorOpeningModule$Client` loads.
* **General notes**.
  * It is recommended to make fewer things `static`. It's easy to obtain module instances with `Quark.ZETA.modules.get`.

In the interim period, modules annotated with `@LoadModule` instead of the new `@ZetaLoadModule` are subscribed to the Forge event bus. This is just to keep the rest of the mod on life support and it *is* imperfect - many QuarkModules are broken on the dedicated server (it's not safe to subscribe *at all* to *any* annotation-based event bus - even Zeta's - if you have even one `@OnlyIn` method, so Zeta can't subscribe these modules to the *load* event bus either, so they don't receive lifecycle events or register blocks...)

So tl;dr for zeta-fying a module:

* swap LoadModule to ZetaLoadModule, remove hasSubscriptions/subscribeOn
* move everything marked `@OnlyIn(Dist.CLIENT)` (and all `@SubscribeEvent`s if subscribeOn was formerly `Dist.CLIENT` only) into a client module replacement,
* create cross-platform versions of any missing `@SubscribeEvent`s in Zeta and subscribe to them with `@PlayEvent`,
* remove all other mentions of Forge and add an appropriate indirection layer in Zeta.

Repeat ~~164x~~ ~~136x~~ 47x.

# The initial Zeta pitch

Zeta is a framework for writing *modular*, *configurable*, and *portable* mods. Zeta helps with the more menial parts of mod-development so you can let your creativity flow.

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

Call `bus.fire()`.

The one-argument `.fire` looks for event listeners corresponding to the event's class. The two-argument `.fire` takes this class as an argument. This is a bit unusual, but allows for events to have a split API and implementation - you can fire `MyEventImpl` as `MyEvent.class` and listeners for `MyEvent` will be triggered.

If an event implements `Cancellable`, calling `cancel` will stop its propagation to the rest of the event listeners.

## Zeta Network

Just the netcode that was already in Quark/ARL tbh

## Zeta Registry

its literally autoreglib. Except its not

# code goals

* Cut down on `static` usage
* Keep the components of Zeta relatively loosely coupled if at all possible

# notes from vazkii

quark as a lot of floating "BlahBlahHandler" classes. Some can stay singletons, others should be made non-static; some are more "utilities" than "handlers".

> I think for these singleton Util type classes (VariantHandler, ToolInteractionHandler, etc) they should be named in a very clear way and all moved to a single package so someone can just look at the package tree and get at a glance a very quick birds eye view of what zeta's structure allows them to do

Some of these fit nicer as event arguments too, instead of singletons that need to be accessed at the right time.

Bring to zeta:

- "If the feature exists only to interact with a specific quark feature it stays in quark, otherwise it goes in zeta"
- [x] Piston logic override in zeta? :eyes:
- [ ] Recipe crawler -> zeta
- [ ] Advancement modification system
- [ ] "ig the worldgen shim?"
- [ ] "pretty much everything in `block` is viable to pull out"
- [x] some stuff wrt to module loader - anti overlap
- [ ] ItemOverrideHandler is used by variant bookshelves/ladders but it's pretty standalone
- [ ] ToolInteractionHandler is "literally only used for waxing" but it's important
- [ ] QuarkBlock and QuarkItem are really for "disableable/enablable blocks"
- [ ] WoodSetHandler is important, but there are some quark uniques in there
- [ ] VanillaWoods is used in a few modules, could be moved out since it's useful

Keep in quark/reform:

- [x] ContributorRewardHandler (ofc)
- [x] CreativeTabHandler will need rethinking for the new creative tab scheme in 1.19.4
  - stub api is in place, CreativeTabHandler is just a bridge to it 
- [ ] EntityAttributeHandler is "essentially just a bridge"
- [x] DyeHandler -> some simple utilities fit in Zeta, others not so much
- [x] capabilities: pain point. Some can be made non-capabilities
  - tried to abstract over all quark capabilities, still gotta deal with IItemHandler and such tho 
- [ ] InventoryTransferHandler is quark specific so it can stay
- [ ] MiscUtil should probably be dissolved
  - ~~(addToLootTable is very important though -> maybe into a LootTableLoadEvent shim)~~ Done
- [ ] SimilarBlockTypeHandler is for quark shulker box stuff
- [ ] UndergroundBiomeHandler is overengineered

Obsolete things:

- [x] "External config" stuff can be removed and does not work anyway
- [x] RenderLayerHandler might be different
- [ ] worldgen can be simplified a bit - just need a way to add a Singular feature to a biome in every step, then everything works itself out

# can you add a note to the todo about datagen

Sure I can do that. quark datagen should be real

# (quat) ok more notes page

## handler vs util

in my head "handler" implies its main purpose is to subscribe to events, and "util" is a collection of static methods

handlers can be stateful. ideally util classes should be entirely static methods with no static fields. because we're trying to be more careful about `static` usage (so multiple instances of Zeta can be initialized by different uncooperating mods)

## config

the config reloading situation is not great imo. There's `IConfigType`, at this point all of the GUI stuff has been ditched and it means exactly two things:

* The `@Config` annotation scanner should descend into me and make a subcategory with my @Config fields
* Give me a call back when the config gets reloaded (also give me the module i belong to).

This is nice since the callback is guaranteed to run immediately after all the `@Config` fields have been updated. But the `ZetaModule`/`ConfigFlagManager` arguments are sorta indicative of a leaky abstraction (why don't config subsections already get access to the module they belong to?), and it's a system only available to these special `IConfigType` subsections but not to modules or other bits of code.

Everything else is relegated to the `ZConfigChanged` event, which doesn't have as strong of an ordering guarantee.

## Event bus ordering, in general

Zeta takes a very event-bus-centric approach to things, but:

* there is a big risk of hidden dependencies, where handler A has to run before handler B but nobody notices
* when you *do* need strong ordering guarantees, it is pretty hard to express them (you need pre/post events, or to cram multiple tasks into a single event handler)

Zeta's event bus doesn't have any "event priority" notion at all, so ive been cheating with `.Pre` and `.Lowest` variants of events.