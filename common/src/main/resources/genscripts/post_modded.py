from jsongen import *

copy([
	('unique/block_model_post_modded.json', 'assets/{modid}/models/block/{name}_post.json'),
	('unique/block_item_post.json', 'assets/{modid}/models/item/{name}_post.json'),
	('unique/blockstate_post.json', 'assets/{modid}/blockstates/{name}_post.json'),

	('unique/recipe_post_modded.json', 'data/{modid}/recipes/building/crafting/{name}_post.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_post'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Post'
))

import update_drop_tables
