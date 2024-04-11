import sys, os, json, re
from os import system as run

modid = 'quark'
category = 'world'
flag = 'flag%' # using % ensures the game explodes if unset
nolog = False

def main():
	global category, flag, nolog
	for arg in sys.argv:
		if not '.py' in arg:
			if '=' in arg:
				toks = arg.split('=')
				if toks[0] == 'category':
					category = toks[1]
				elif toks[0] == 'flag':
					flag = toks[1]
				elif toks[0] == 'nolog' and toks[1] == 'true':
					nolog = True
			else:
				makeWood(arg)

def makeWood(type):
	global category, flag, nolog

	run(f"py generic_block.py {type}_planks")
	if not nolog:
		run(f"py pillar.py {type}_log stripped_{type}_log")
		run(f"py wood_block.py {type} stripped_{type}")
		run(f"py hollow_log.py origspace={modid} {type}_log")

	run(f"py stairs_slabs.py category={category} flag={flag} {type}_planks")
	run(f"py post_modded.py flag={flag} {type} stripped_{type}")
	run(f"py bookshelves.py {type}")
	run(f"py chest.py {type}")
	run(f"py ladder.py {type}")
	run(f"py door.py {type}")
	run(f"py trapdoor.py {type}")
	run(f"py fence_gates.py {type}")
	run(f"py fences.py {type}")
	run(f"py sign.py {type}")
	run(f"py buttons.py texname={type}_planks {type}")
	run(f"py pressure_plates.py texname={type}_planks {type}")
	run(f"py generic_item.py {type}_boat {type}_chest_boat")
	run(f"py vertical_planks.py vertical_{type}_planks")
	run(f"py vertical_plank_recipes.py {type}")

	run(f"py wood_set_recipes.py category={category} flag={flag} {type}")

	appendTags(type)

def appendTags(type):
	global category, flag, nolog, modid

	if nolog:
		addToTag('mineable/axe', type, ["%_planks", "%_post",
			"stripped_%_post", "%_bookshelf", "%_planks_slab", "%_planks_stairs",
			"%_planks_vertical_slab", "%_fence", "%_fence_gate", "%_door",
			 "%_trapdoor", "%_ladder", "%_sign", "%_wall_sign", "%_chest",
			 "%_trapped_chest", "%_button", "%_pressure_plate"], False)
	else:
		addToTag('mineable/axe', type, ["%_planks", "%_log", "%_wood",
			"stripped_%_log", "stripped_%_wood", "%_post",
			"stripped_%_post", "%_bookshelf", "%_planks_slab", "%_planks_stairs",
			"%_planks_vertical_slab", "%_fence", "%_fence_gate", "%_door",
			 "%_trapdoor", "%_ladder", "%_sign", "%_wall_sign", "%_chest",
			 "%_trapped_chest", "%_button", "%_pressure_plate"], False)
		addToTag('overworld_natural_logs', type, ["%_log"])
		bulkTag(['logs', 'logs_that_burn', f"{modid}:{type}_logs"], type, ["%_log", "stripped_%_log", "%_wood", "stripped_%_wood"])

	addToTag('quark:ladders', type, ["%_ladder"])
	addToTag('climbable', type, ["%_ladder"], False)
	addToTag('planks', type, ["%_planks", "vertical_%_planks"])
	addToTag('wooden_stairs', type, ["%_planks_stairs"])
	addToTag('wooden_slabs', type, ["%_planks_slab"])
	addToTag('quark:wooden_vertical_slabs', type, ["%_planks_vertical_slab"])
	bulkTag(['trapdoors', 'wooden_trapdoors'], type, ["%_trapdoor"])
	bulkTag(['fences', 'wooden_fences', 'forge:fences', 'forge:fences/wooden'], type, ["%_fence"])
	bulkTag(['fence_gates', 'forge:fence_gates', 'forge:fence_gates/wooden'], type, ["%_fence_gate"])
	bulkTag(['buttons', 'wooden_buttons'], type, ["%_button"])
	bulkTag(['pressure_plates', 'pressure_plates', 'wooden_pressure_plates'], type, ["%_pressure_plate"])
	bulkTag(['doors', 'wooden_doors'], type, ["%_door"])
	addToTag('forge:bookshelves', type, ["%_bookshelf"])
	bulkTag(['signs', 'standing_signs'], type, ["%_sign"], False)
	bulkTag(['signs', 'wall_signs'], type, ["%_wall_sign"], False)
	bulkTag(['forge:chests', 'forge:chests/wooden', 'guarded_by_piglins'], type, ["%_chest", "%_trapped_chest"])
	addToTag('forge:chests/trapped', type, ["%_trapped_chest"])
	addToTag('quark:hollow_logs', type, ["hollow_%_log"])

def bulkTag(tags, type, items, mirror=True, is_block=True):
	for tag in tags:
		addToTag(tag, type, items, mirror, is_block)

def addToTag(tag, type, items, mirror=True, is_block=True):
	if mirror:
		addToTag(tag, type, items, False, not is_block)

	if ':' in tag:
		resloc = tag.split(':')
	else:
		resloc = ['minecraft', tag]

	tag_type = 'blocks' if is_block else 'items'
	path = f"../data/{resloc[0]}/tags/{tag_type}/{resloc[1]}.json"

	if not os.path.exists(path):
		parent = re.sub(r'/[^/]+$', '', path)
		os.makedirs(parent, exist_ok=True)

		with open(path, 'w') as f:
			f.write('{ "replace": false, "values": [] }')

	with open(path, 'r') as f:
		data = json.load(f)
		values = data['values']

		changed = False
		for raw_item in items:
			item = f"{modid}:{raw_item}"
			item = item.replace('%', type)

			if not item in values:
				values.append(item)
				changed = True

		if changed:
			with open(path, 'w') as fw:
				json.dump(data, fw, indent=4)

main()
