from jsongen import *

copy([
	('blockstate_generic.json', 'assets/{modid}/blockstates/{name}.json'),
	('drop_table_generic.json', 'data/{modid}/loot_tables/blocks/{name}.json')
])

localize_standard('block')

import update_drop_tables