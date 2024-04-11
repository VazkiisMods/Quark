from jsongen import *

copy([
	('block_model_wood_block.json', 'assets/{modid}/models/block/{name}_wood.json'),
	('block_item_wood_block.json', 'assets/{modid}/models/item/{name}_wood.json'),
	('blockstate_wood_block.json', 'assets/{modid}/blockstates/{name}_wood.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_wood'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Wood'
))

import update_drop_tables