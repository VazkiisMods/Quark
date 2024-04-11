from jsongen import *
import re

# Use category=... and flag=... to set the flags for this one

copy([
	('block_model_wall_side_tall.json', 'assets/{modid}/models/block/{name}_wall_side_tall.json'),
	('blockstate_wall.json', 'assets/{modid}/blockstates/{name}_wall.json')
])