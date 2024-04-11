from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_pane_nosuffix_side.json', 'assets/{modid}/models/block/{name}_side.json'),
	('block_model_pane_nosuffix_noside.json', 'assets/{modid}/models/block/{name}_noside.json'),
	('block_model_pane_nosuffix_side_alt.json', 'assets/{modid}/models/block/{name}_side_alt.json'),
	('block_model_pane_nosuffix_noside_alt.json', 'assets/{modid}/models/block/{name}_noside_alt.json'),
	('block_model_pane_nosuffix_post.json', 'assets/{modid}/models/block/{name}_post.json'),
	('block_item_flat.json', 'assets/{modid}/models/item/{name}.json'),

	('blockstate_pane_nosuffix.json', 'assets/{modid}/blockstates/{name}.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}'.format(name = name, modid = modid),
	lambda name, modid: localize_name(name, modid)
))

import update_drop_tables
