from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('unique/recipe_stone_bricks.json', 'data/{modid}/recipes/building/crafting/stonevariants/{name}_bricks.json'),
	('unique/recipe_stone_chiseled.json', 'data/{modid}/recipes/building/crafting/stonevariants/chiseled_{name}_bricks.json'),
	('unique/recipe_stone_pillar.json', 'data/{modid}/recipes/building/crafting/stonevariants/{name}_pillar.json'),
])

import update_drop_tables
