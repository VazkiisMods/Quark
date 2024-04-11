from jsongen import *

copy([
	('unique/item_model_spawn_egg.json', 'assets/{modid}/models/item/{name}_spawn_egg.json')
])

localize((
	lambda name, modid: 'item.{modid}.{name}_spawn_egg'.format(name = name, modid = modid),
	lambda name, modid: re.sub(r's$', '', localize_name(name, modid)) + ' Spawn Egg'
))
