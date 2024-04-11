from jsongen import *
import os
import re

generic_targets = []
slab_targets = []
silk_targets = []
named_targets = []

named_pattern = re.compile(r'^(?:(?:(?:[a-z]+_)+chest)|matrix_enchanter|feeding_trough|crate)$')
frame_pattern = re.compile(r'(?:[a-z]+_)+frame$')

for file in os.listdir('../assets/quark/blockstates'):
	if '.json' in file:
		test = '../data/quark/loot_tables/blocks/' + file
		if not os.path.exists(test):
			block_name = file.replace('.json', '')
			if '_speleothem' in block_name:
				silk_targets.append(block_name)
			elif '_slab' in block_name:
				slab_targets.append(block_name)
			elif named_pattern.match(block_name):
				named_targets.append(block_name)
			elif not frame_pattern.match(block_name):
				generic_targets.append(block_name)

foreach_arg_array(0, generic_targets, [('drop_table_generic.json', 'data/{modid}/loot_tables/blocks/{name}.json')], copy_callback)
foreach_arg_array(0, slab_targets, [('drop_table_slab.json', 'data/{modid}/loot_tables/blocks/{name}.json')], copy_callback)
foreach_arg_array(0, silk_targets, [('drop_table_silk.json', 'data/{modid}/loot_tables/blocks/{name}.json')], copy_callback)
foreach_arg_array(0, named_targets, [('drop_table_named.json', 'data/{modid}/loot_tables/blocks/{name}.json')], copy_callback)
