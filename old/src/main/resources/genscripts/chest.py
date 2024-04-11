from jsongen import *

copy([
	('unique/chest/blockstate.json', 'assets/{modid}/blockstates/{name}_chest.json'),
	('unique/chest/blockstate.json', 'assets/{modid}/blockstates/{name}_trapped_chest.json'),

	('unique/chest/block_model.json', 'assets/{modid}/models/block/{name}_chest.json'),

	('unique/chest/item_model.json', 'assets/{modid}/models/item/{name}_chest.json'),
	('unique/chest/item_model.json', 'assets/{modid}/models/item/{name}_trapped_chest.json'),

	('unique/chest/recipe.json', 'data/{modid}/recipes/building/crafting/chests/compat/{name}_chest.json'),
	('unique/chest/recipe_trapped.json', 'data/{modid}/recipes/building/crafting/chests/compat/{name}_chest_trapped.json'),
	('unique/chest/recipe_utility.json', 'data/{modid}/recipes/tweaks/crafting/utility/chests/compat/{name}_chest_wood.json'),
])

localize((
	lambda name, modid: 'block.{modid}.{name}_chest'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Chest'
))

localize((
	lambda name, modid: 'block.{modid}.{name}_trapped_chest'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Trapped Chest'
))

import update_drop_tables
