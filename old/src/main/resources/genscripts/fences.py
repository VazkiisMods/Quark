from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_fence_post.json', 'assets/{modid}/models/block/{name}_fence_post.json'),
	('block_model_fence_side.json', 'assets/{modid}/models/block/{name}_fence_side.json'),
	('block_model_fence_inventory.json', 'assets/{modid}/models/block/{name}_fence_inventory.json'),

	('block_item_fence.json', 'assets/{modid}/models/item/{name}_fence.json'),
	('blockstate_fence.json', 'assets/{modid}/blockstates/{name}_fence.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_fence'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Fence'
))

import update_tags
import update_drop_tables
