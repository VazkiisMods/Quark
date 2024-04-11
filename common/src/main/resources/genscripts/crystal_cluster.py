from jsongen import *

copy([
	('unique/block_model_crystal_cluster.json', 'assets/{modid}/models/block/{name}.json'),
	('item_model_generic.json', 'assets/{modid}/models/item/{name}.json'),
	('unique/blockstate_crystal_cluster.json', 'assets/{modid}/blockstates/{name}.json')
])

import update_drop_tables
