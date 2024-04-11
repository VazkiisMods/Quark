from jsongen import *

# Use category=... and flag=... to set the flags for this one

copy([
	('recipe_slab_stonecutter.json', 'data/{modid}/recipes/{category}/stonecutting/slabs/{name}_slab_stonecutter.json'),
	('recipe_vertical_slab_stonecutter.json', 'data/{modid}/recipes/building/stonecutting/vertslabs/{name}_vertical_slab_stonecutter.json')
])

import slabs