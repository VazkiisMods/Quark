from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('unique/recipe_stone_bricks_stonecutter.json', 'data/{modid}/recipes/building/stonecutting/stonevariants/{name}_bricks_stonecutter.json'),
	('unique/recipe_stone_chiseled_stonecutter.json', 'data/{modid}/recipes/building/stonecutting/stonevariants/chiseled_{name}_bricks_stonecutter.json'),
	('unique/recipe_stone_pillar_stonecutter.json', 'data/{modid}/recipes/building/stonecutting/stonevariants/{name}_pillar_stonecutter.json')
])

import update_drop_tables
