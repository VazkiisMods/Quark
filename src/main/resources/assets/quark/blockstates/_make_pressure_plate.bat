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
	echo Making %%x_pressure_plate.json
	(
		echo {
		echo 	"forge_marker": 1,
		echo 	"defaults": {
		echo 		"model": "minecraft:pressure_plate_up",
		echo 		"textures": {
		echo 			"texture": "minecraft:blocks/planks_%%x"
		echo 		}
		echo 	},
		echo 	"variants": {
		echo 		"normal": [{}],
		echo 		"inventory": [{}],
		echo 		"powered=false": [{}],
		echo 		"powered=true": [{ "model": "minecraft:pressure_plate_down" }]
		echo 	}
		echo }
	) > %%x_pressure_plate.json

)
