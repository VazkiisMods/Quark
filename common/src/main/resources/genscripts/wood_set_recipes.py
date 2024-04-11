from jsongen import *

def main():
	copy_tuples([
		'planks', 'wood', 'stripped_wood', 'fence', 'sign', 'button', 'pressure_plate', 'door', 'trapdoor',
		'fence_gate', 'boat', 'chest_boat', 'bookshelf', 'chest', 'chest_wood', 'trapped_chest', 'ladder'
	])

def copy_tuples(values):
	tuple_list = []
	for val in values:
		recipe = val
		recipe_type = 'crafting'
		if '|' in val:
			toks = val.split('|')
			recipe = toks[0]
			recipe_type = toks[1]

		tuple_list.append(wsr_tuple(recipe, recipe_type))
	copy(tuple_list)

def wsr_tuple(name, recipe_type):
	return ('wsr/' +  name + '.json', 'data/{modid}/recipes/{category}/' + recipe_type + '/woodsets/{name}/' + name + '.json')

main()