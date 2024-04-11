from jsongen import *

copy([
	('block_model_ladder.json', 'assets/{modid}/models/block/{name}_ladder.json'),
	('block_item_ladder.json', 'assets/{modid}/models/item/{name}_ladder.json'),
	('blockstate_ladder.json', 'assets/{modid}/blockstates/{name}_ladder.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_ladder'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Ladder'
))

import update_drop_tables