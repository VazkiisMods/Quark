from jsongen import *

copy([
	('unique/block_model_sign.json', 'assets/{modid}/models/block/{name}_sign.json'),
	('blockstate_sign.json', 'assets/{modid}/blockstates/{name}_sign.json'),
	('blockstate_sign.json', 'assets/{modid}/blockstates/{name}_wall_sign.json'),
	('block_item_sign.json', 'assets/{modid}/models/item/{name}_sign.json'),

	('loot_table_wall_sign.json', 'data/{modid}/loot_tables/blocks/{name}_wall_sign.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_sign'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Sign'
))

import update_drop_tables
