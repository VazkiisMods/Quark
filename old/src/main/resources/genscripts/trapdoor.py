from jsongen import *

copy([
	('block_model_trapdoor_top.json', 'assets/{modid}/models/block/{name}_trapdoor_top.json'),
	('block_model_trapdoor_bottom.json', 'assets/{modid}/models/block/{name}_trapdoor_bottom.json'),
	('block_model_trapdoor_open.json', 'assets/{modid}/models/block/{name}_trapdoor_open.json'),

	('block_item_trapdoor.json', 'assets/{modid}/models/item/{name}_trapdoor.json'),
	('blockstate_trapdoor.json', 'assets/{modid}/blockstates/{name}_trapdoor.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_trapdoor'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Trapdoor'
))

import update_drop_tables