@echo off
:: Vazkii's JSON creator for blocks
:: Put in your /resources/assets/%modid%/models/block
:: Makes basic block JSON files as well as the acossiated item and simple blockstate
:: Can make multiple blocks at once
::
:: Usage:
:: _make (block name 1) (block name 2) (block name x)
::
:: Change this to your mod's ID
set modid=quark

setlocal enabledelayedexpansion

for %%x in (%*) do (
	echo Making %%x_slab.json
	(
		echo {
		echo   "forge_marker": 1,
		echo   "defaults": {
		echo     "model": "minecraft:half_slab",
		echo     "textures": {
		echo       "bottom": "%modid%:blocks/%%x",
		echo       "top": "%modid%:blocks/%%x",
		echo       "side": "%modid%:blocks/%%x"
		echo     },
		echo     "transform": "forge:default-block"
		echo   },
		echo   "variants": {
		echo     "half=top": [{
		echo       "x": 180,
		echo       "uvlock": true
		echo     }],
		echo     "half=bottom": [{}],
		echo     "inventory": [{}]
		echo   }
		echo }
	) > %%x_slab.json

	echo Making %%x_slab_double.json
	(
		echo {
		echo	"forge_marker": 1,
		echo	"defaults": {
		echo		"model": "minecraft:cube_all",
		echo		"transform": "forge:default-block"
		echo	},
		echo 	"variants": {
		echo 		"normal": [{ "textures": { "all": "%modid%:blocks/%%x" } }],
		echo 		"prop=blarg": [{ "textures": { "all": "%modid%:blocks/%%x" } }]
		echo 	}
		echo }
	) > %%x_slab_double.json

	echo Making %%x_stairs.json
	(
		echo {
		echo   "forge_marker": 1,
		echo   "defaults": {
		echo     "textures": {
		echo       "bottom": "%modid%:blocks/%%x",
		echo       "top": "%modid%:blocks/%%x",
		echo       "side": "%modid%:blocks/%%x"
		echo     }
		echo   },
		echo   "variants": {
		echo      "normal": { "model": "minecraft:stairs" },
		echo      "inventory": { "model": "minecraft:stairs" },
		echo		"facing=east,half=bottom,shape=straight":  { "model": "minecraft:stairs" },
		echo		"facing=west,half=bottom,shape=straight":  { "model": "minecraft:stairs", "y": 180, "uvlock": true },
		echo		"facing=south,half=bottom,shape=straight": { "model": "minecraft:stairs", "y": 90, "uvlock": true },
		echo		"facing=north,half=bottom,shape=straight": { "model": "minecraft:stairs", "y": 270, "uvlock": true },
		echo		"facing=east,half=bottom,shape=outer_right":  { "model": "minecraft:outer_stairs" },
		echo		"facing=west,half=bottom,shape=outer_right":  { "model": "minecraft:outer_stairs", "y": 180, "uvlock": true },
		echo		"facing=south,half=bottom,shape=outer_right": { "model": "minecraft:outer_stairs", "y": 90, "uvlock": true },
		echo		"facing=north,half=bottom,shape=outer_right": { "model": "minecraft:outer_stairs", "y": 270, "uvlock": true },
		echo		"facing=east,half=bottom,shape=outer_left":  { "model": "minecraft:outer_stairs", "y": 270, "uvlock": true },
		echo		"facing=west,half=bottom,shape=outer_left":  { "model": "minecraft:outer_stairs", "y": 90, "uvlock": true },
		echo		"facing=south,half=bottom,shape=outer_left": { "model": "minecraft:outer_stairs" },
		echo		"facing=north,half=bottom,shape=outer_left": { "model": "minecraft:outer_stairs", "y": 180, "uvlock": true },
		echo		"facing=east,half=bottom,shape=inner_right":  { "model": "minecraft:inner_stairs" },
		echo		"facing=west,half=bottom,shape=inner_right":  { "model": "minecraft:inner_stairs", "y": 180, "uvlock": true },
		echo		"facing=south,half=bottom,shape=inner_right": { "model": "minecraft:inner_stairs", "y": 90, "uvlock": true },
		echo		"facing=north,half=bottom,shape=inner_right": { "model": "minecraft:inner_stairs", "y": 270, "uvlock": true },
		echo		"facing=east,half=bottom,shape=inner_left":  { "model": "minecraft:inner_stairs", "y": 270, "uvlock": true },
		echo		"facing=west,half=bottom,shape=inner_left":  { "model": "minecraft:inner_stairs", "y": 90, "uvlock": true },
		echo		"facing=south,half=bottom,shape=inner_left": { "model": "minecraft:inner_stairs" },
		echo		"facing=north,half=bottom,shape=inner_left": { "model": "minecraft:inner_stairs", "y": 180, "uvlock": true },
		echo		"facing=east,half=top,shape=straight":  { "model": "minecraft:stairs", "x": 180, "uvlock": true },
		echo		"facing=west,half=top,shape=straight":  { "model": "minecraft:stairs", "x": 180, "y": 180, "uvlock": true },
		echo		"facing=south,half=top,shape=straight": { "model": "minecraft:stairs", "x": 180, "y": 90, "uvlock": true },
		echo		"facing=north,half=top,shape=straight": { "model": "minecraft:stairs", "x": 180, "y": 270, "uvlock": true },
		echo		"facing=east,half=top,shape=outer_right":  { "model": "minecraft:outer_stairs", "x": 180, "y": 90, "uvlock": true },
		echo		"facing=west,half=top,shape=outer_right":  { "model": "minecraft:outer_stairs", "x": 180, "y": 270, "uvlock": true },
		echo		"facing=south,half=top,shape=outer_right": { "model": "minecraft:outer_stairs", "x": 180, "y": 180, "uvlock": true },
		echo		"facing=north,half=top,shape=outer_right": { "model": "minecraft:outer_stairs", "x": 180, "uvlock": true },
		echo		"facing=east,half=top,shape=outer_left":  { "model": "minecraft:outer_stairs", "x": 180, "uvlock": true },
		echo		"facing=west,half=top,shape=outer_left":  { "model": "minecraft:outer_stairs", "x": 180, "y": 180, "uvlock": true },
		echo		"facing=south,half=top,shape=outer_left": { "model": "minecraft:outer_stairs", "x": 180, "y": 90, "uvlock": true },
		echo		"facing=north,half=top,shape=outer_left": { "model": "minecraft:outer_stairs", "x": 180, "y": 270, "uvlock": true },
		echo		"facing=east,half=top,shape=inner_right":  { "model": "minecraft:inner_stairs", "x": 180, "y": 90, "uvlock": true },
		echo		"facing=west,half=top,shape=inner_right":  { "model": "minecraft:inner_stairs", "x": 180, "y": 270, "uvlock": true },
		echo		"facing=south,half=top,shape=inner_right": { "model": "minecraft:inner_stairs", "x": 180, "y": 180, "uvlock": true },
		echo		"facing=north,half=top,shape=inner_right": { "model": "minecraft:inner_stairs", "x": 180, "uvlock": true },
		echo		"facing=east,half=top,shape=inner_left":  { "model": "minecraft:inner_stairs", "x": 180, "uvlock": true },
		echo		"facing=west,half=top,shape=inner_left":  { "model": "minecraft:inner_stairs", "x": 180, "y": 180, "uvlock": true },
		echo		"facing=south,half=top,shape=inner_left": { "model": "minecraft:inner_stairs", "x": 180, "y": 90, "uvlock": true },
		echo		"facing=north,half=top,shape=inner_left": { "model": "minecraft:inner_stairs", "x": 180, "y": 270, "uvlock": true }
		echo   }
		echo }
	) > %%x_stairs.json
)
