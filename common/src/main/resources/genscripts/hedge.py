from jsongen import *

copy([
	('unique/block_model_hedge_post.json', 'assets/{modid}/models/block/{name}_hedge_post.json'),
	('unique/block_model_hedge_extend.json', 'assets/{modid}/models/block/{name}_hedge_extend.json'),
	('unique/block_model_hedge_side.json', 'assets/{modid}/models/block/{name}_hedge_side.json'),
	('unique/block_item_hedge.json', 'assets/{modid}/models/item/{name}_hedge.json'),
	('unique/blockstate_hedge.json', 'assets/{modid}/blockstates/{name}_hedge.json'),

	('unique/recipe_hedge.json', 'data/{modid}/recipes/building/crafting/{name}_hedge.json')
])

import update_drop_tables
