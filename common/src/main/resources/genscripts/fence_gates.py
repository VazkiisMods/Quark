from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_gate.json', 'assets/{modid}/models/block/{name}_fence_gate.json'),
	('block_model_gate_open.json', 'assets/{modid}/models/block/{name}_fence_gate_open.json'),
	('block_model_gate_wall.json', 'assets/{modid}/models/block/{name}_fence_gate_wall.json'),
	('block_model_gate_wall_open.json', 'assets/{modid}/models/block/{name}_fence_gate_wall_open.json'),
	('block_item_gate.json', 'assets/{modid}/models/item/{name}_fence_gate.json'),

	('blockstate_gate.json', 'assets/{modid}/blockstates/{name}_fence_gate.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_fence_gate'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Fence Gate'
))

import update_tags
import update_drop_tables
