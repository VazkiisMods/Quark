from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_slab.json', 'assets/{modid}/models/block/{name}_slab.json'),
	('block_model_slab_top.json', 'assets/{modid}/models/block/{name}_slab_top.json'),
	('block_item_slab.json', 'assets/{modid}/models/item/{name}_slab.json'),
	('blockstate_slab.json', 'assets/{modid}/blockstates/{name}_slab.json'),
	
	('recipe_slab.json', 'data/{modid}/recipes/{category}/crafting/slabs/{name}_slab.json'),

	('block_model_vertical_slab.json', 'assets/{modid}/models/block/{name}_vertical_slab.json'),
	('block_item_vertical_slab.json', 'assets/{modid}/models/item/{name}_vertical_slab.json'),
	('blockstate_vertical_slab.json', 'assets/{modid}/blockstates/{name}_vertical_slab.json'),

	('recipe_vertical_slab.json', 'data/{modid}/recipes/building/crafting/vertslabs/{name}_vertical_slab.json'),
	('recipe_vertical_slab_revert.json', 'data/{modid}/recipes/building/crafting/vertslabs/{name}_vertical_slab_revert.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_slab'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Slab'
))

localize((
	lambda name, modid: 'block.{modid}.{name}_vertical_slab'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Vertical Slab'
))

import update_tags
import update_drop_tables