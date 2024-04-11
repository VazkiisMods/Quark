from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([	
	('block_model_vertical_slab_vanilla.json', 'assets/{modid}/models/block/{name}_vertical_slab.json'),
	('block_item_vertical_slab.json', 'assets/{modid}/models/item/{name}_vertical_slab.json'),
	('blockstate_vertical_slab.json', 'assets/{modid}/blockstates/{name}_vertical_slab.json'),

	('recipe_vertical_slab_vanilla.json', 'data/{modid}/recipes/building/crafting/vertslabs/{name}_vertical_slab.json'),
	('recipe_vertical_slab_revert_vanilla.json', 'data/{modid}/recipes/building/crafting/vertslabs/{name}_vertical_slab_revert.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_vertical_slab'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Vertical Slab'
))

import update_tags
import update_drop_tables