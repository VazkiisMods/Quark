from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_plate_down.json', 'assets/{modid}/models/block/{name}_pressure_plate_down.json'),
	('block_model_plate.json', 'assets/{modid}/models/block/{name}_pressure_plate.json'),
	('block_item_plate.json', 'assets/{modid}/models/item/{name}_pressure_plate.json'),

	('blockstate_plate.json', 'assets/{modid}/blockstates/{name}_pressure_plate.json')
])

localize((
	lambda name, modid: 'block.{modid}.{name}_pressure_plate'.format(name = name, modid = modid),
	lambda name, modid: localize_name(name, modid) + ' Pressure Plate'
))

import update_drop_tables
