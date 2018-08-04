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
	
	echo Making %%x_button.json
	(
		echo {
		echo 	"forge_marker": 1,
		echo 	"defaults": {
		echo 		"model": "minecraft:button",
		echo 		"textures": {
		echo 			"texture": "minecraft:blocks/planks_%%x"
		echo 		}
		echo 	},
		echo     "variants": {
		echo         "inventory":                  { "model": "minecraft:button_inventory" },
		echo         "facing=up,powered=false":    { "model": "minecraft:button", "uvlock": true },
		echo         "facing=up,powered=false":    { "model": "minecraft:button", "uvlock": true },
		echo         "facing=down,powered=false":  { "model": "minecraft:button", "uvlock": true, "x": 180 },
		echo         "facing=east,powered=false":  { "model": "minecraft:button", "uvlock": true, "x": 90, "y": 90 },
		echo         "facing=west,powered=false":  { "model": "minecraft:button", "uvlock": true, "x": 90, "y": 270 },
		echo         "facing=south,powered=false": { "model": "minecraft:button", "uvlock": true, "x": 90, "y": 180 },
		echo         "facing=north,powered=false": { "model": "minecraft:button", "uvlock": true, "x": 90 },
		echo         "facing=up,powered=true":     { "model": "minecraft:button_pressed", "uvlock": true },
		echo         "facing=down,powered=true":   { "model": "minecraft:button_pressed", "uvlock": true, "x": 180 },
		echo         "facing=east,powered=true":   { "model": "minecraft:button_pressed", "uvlock": true, "x": 90, "y": 90 },
		echo         "facing=west,powered=true":   { "model": "minecraft:button_pressed", "uvlock": true, "x": 90, "y": 270 },
		echo         "facing=south,powered=true":  { "model": "minecraft:button_pressed", "uvlock": true, "x": 90, "y": 180 },
		echo         "facing=north,powered=true":  { "model": "minecraft:button_pressed", "uvlock": true, "x": 90 }
		echo     }
		echo }
	) > %%x_button.json

)
