from jsongen import *

copy([
	('unique/recipe_vertical_planks.json', 'data/{modid}/recipes/building/crafting/vertical_{name}_planks.json'),
	('unique/recipe_vertical_planks_revert.json', 'data/{modid}/recipes/building/crafting/vertical_{name}_planks_revert.json')
])

import update_drop_tables