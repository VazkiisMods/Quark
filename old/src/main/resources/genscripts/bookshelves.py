from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_bookshelf.json', 'assets/{modid}/models/block/{name}_bookshelf.json'),
	('block_item_bookshelf.json', 'assets/{modid}/models/item/{name}_bookshelf.json'),

	('blockstate_bookshelf.json', 'assets/{modid}/blockstates/{name}_bookshelf.json'),
	('loot_table_bookshelf.json', 'data/{modid}/loot_tables/blocks/{name}_bookshelf.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_bookshelf'.format(name = name, modid = modid),
	lambda name, modid: localize_name(name, modid) + ' Bookshelf'
))

import update_tags
import update_drop_tables
