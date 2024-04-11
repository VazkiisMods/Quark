from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_button.json', 'assets/{modid}/models/block/{name}_button.json'),
	('block_model_button_pressed.json', 'assets/{modid}/models/block/{name}_button_pressed.json'),
	('block_item_button.json', 'assets/{modid}/models/item/{name}_button.json'),

	('blockstate_button.json', 'assets/{modid}/blockstates/{name}_button.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_button'.format(name = name, modid = modid),
	lambda name, modid: localize_name(name, modid) + ' Button'
))

import update_tags
import update_drop_tables
