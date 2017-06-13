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

	echo Making colored_flowerpot_%%x.json
	(
        echo {
        echo     "forge_marker": 1,
        echo     "defaults": {
        echo         "model": "minecraft:cube_all",
        echo         "transform": "forge:default-block",
        echo         "textures": { 
        echo             "flowerpot": "quark:blocks/flower_pot_stained_%%x", 
        echo             "particle": "quark:blocks/flower_pot_stained_%%x"  
        echo         }
        echo     },
        echo     "variants": {
        echo         "contents=empty":            { "model": "flower_pot" },
        echo         "contents=rose":             { "model": "flower_pot_rose" },
        echo         "contents=blue_orchid":      { "model": "flower_pot_orchid" },
        echo         "contents=allium":           { "model": "flower_pot_allium" },
        echo         "contents=houstonia":        { "model": "flower_pot_houstonia" },
        echo         "contents=red_tulip":        { "model": "flower_pot_tulip_red" },
        echo         "contents=orange_tulip":     { "model": "flower_pot_tulip_orange" },
        echo         "contents=white_tulip":      { "model": "flower_pot_tulip_white" },
        echo         "contents=pink_tulip":       { "model": "flower_pot_tulip_pink" },
        echo         "contents=oxeye_daisy":      { "model": "flower_pot_daisy" },
        echo         "contents=dandelion":        { "model": "flower_pot_dandelion" },
        echo         "contents=oak_sapling":      { "model": "flower_pot_oak" },
        echo         "contents=spruce_sapling":   { "model": "flower_pot_spruce" },
        echo         "contents=birch_sapling":    { "model": "flower_pot_birch" },
        echo         "contents=jungle_sapling":   { "model": "flower_pot_jungle" },
        echo         "contents=acacia_sapling":   { "model": "flower_pot_acacia" },
        echo         "contents=dark_oak_sapling": { "model": "flower_pot_dark_oak" },
        echo         "contents=mushroom_red":     { "model": "flower_pot_mushroom_red" },
        echo         "contents=mushroom_brown":   { "model": "flower_pot_mushroom_brown" },
        echo         "contents=dead_bush":        { "model": "flower_pot_dead_bush" },
        echo         "contents=fern":             { "model": "flower_pot_fern" },
        echo         "contents=cactus":           { "model": "flower_pot_cactus" }
        echo     }
        echo }
	) > colored_flowerpot_%%x.json

    echo Making colored_flowerpot_%%x.json item
    (
        echo {
        echo     "parent": "item/generated",
        echo     "textures": { 
        echo         "layer0": "quark:items/flower_pot_stained_%%x"
        echo     }
        echo }
    ) > ../models/item/colored_flowerpot_%%x.json

)