from jsongen import *

# use %origspace% to define original mod (minecraft/quark)
copy([
	('unique/hollowlog/blockmodel.json', 'assets/{modid}/models/block/hollow_{name}.json'),
	('unique/hollowlog/blockmodel_horizontal.json', 'assets/{modid}/models/block/hollow_{name}_horizontal.json'),
	('unique/hollowlog/itemmodel.json', 'assets/{modid}/models/item/hollow_{name}.json'),
	('unique/hollowlog/blockstate.json', 'assets/{modid}/blockstates/hollow_{name}.json'),

	('unique/hollowlog/recipe.json', 'data/{modid}/recipes/building/crafting/hollowlogs/hollow_{name}.json')
])

localize((
	lambda name, modid: 'block.{modid}.hollow_{name}'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', 'Hollow ' + localize_name(name, modid))
))

import update_drop_tables
