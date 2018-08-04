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

	echo Making %%x_speleothem.json
	(
        echo {
        echo     "forge_marker": 1,
        echo     "defaults": {
        echo         "model": "minecraft:cube_all",
        echo         "transform": "forge:default-block",
        echo         "textures": {
        echo             "block": "minecraft:blocks/stone_%%x"
        echo         }
        echo     },
        echo     "variants": {
        echo         "inventory": [{ "model": "quark:speleothem_medium" }],
        echo         "size=small": [{ "model": "quark:speleothem_small" }],
        echo         "size=medium": [{ "model": "quark:speleothem_medium" }],
        echo         "size=big": [{ "model": "quark:speleothem_big" }]
        echo     }
        echo }

	) > %%x_speleothem.json

)