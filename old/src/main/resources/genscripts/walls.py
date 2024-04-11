from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_wall_side.json', 'assets/{modid}/models/block/{name}_wall_side.json'),
	('block_model_wall_side_tall.json', 'assets/{modid}/models/block/{name}_wall_side_tall.json'),
	('block_model_wall_post.json', 'assets/{modid}/models/block/{name}_wall_post.json'),
	('block_model_wall_inventory.json', 'assets/{modid}/models/block/{name}_wall_inventory.json'),
	('block_item_wall.json', 'assets/{modid}/models/item/{name}_wall.json'),

	('blockstate_wall.json', 'assets/{modid}/blockstates/{name}_wall.json'),

	('recipe_wall.json', 'data/{modid}/recipes/{category}/crafting/walls/{name}_wall.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_wall'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Wall'
))

import update_tags
import update_drop_tables