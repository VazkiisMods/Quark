from jsongen import *

copy([
	('block_model_door_bottom_left.json', 'assets/{modid}/models/block/{name}_door_bottom_left.json'),
	('block_model_door_bottom_left_open.json', 'assets/{modid}/models/block/{name}_door_bottom_left_open.json'),
	('block_model_door_bottom_right.json', 'assets/{modid}/models/block/{name}_door_bottom_right.json'),
	('block_model_door_bottom_right_open.json', 'assets/{modid}/models/block/{name}_door_bottom_right_open.json'),
	('block_model_door_top_left.json', 'assets/{modid}/models/block/{name}_door_top_left.json'),
	('block_model_door_top_left_open.json', 'assets/{modid}/models/block/{name}_door_top_left_open.json'),
	('block_model_door_top_right.json', 'assets/{modid}/models/block/{name}_door_top_right.json'),
	('block_model_door_top_right_open.json', 'assets/{modid}/models/block/{name}_door_top_right_open.json'),

	('block_item_door.json', 'assets/{modid}/models/item/{name}_door.json'),
	('blockstate_door.json', 'assets/{modid}/blockstates/{name}_door.json'),
	('loot_table_door.json', 'data/{modid}/loot_tables/blocks/{name}_door.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_door'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Door'
))

import update_drop_tables