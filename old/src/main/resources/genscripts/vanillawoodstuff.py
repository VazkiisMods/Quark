from jsongen import *
import re, os

import os

def main():  
	rootdir = 'templates/vanillawoodstuff'
	tuples = []
	add_tuples(tuples, rootdir)
	generate(tuples)

def add_tuples(tuples, dir):
	for file in os.listdir(dir):
		fpath = os.path.join(dir, file)
		if os.path.isdir(fpath):
			add_tuples(tuples, fpath)
		else: 
			add_tuple(tuples, fpath)
 

def add_tuple(tuples, f):
	raw = f.replace('templates/', '').replace('\\', '/')

	print(raw)
	tpl_from = raw
	tpl_to = raw.replace('vanillawoodstuff/', '').replace('modid', '{modid}').replace('NAME', '{name}')
	tuples.append((tpl_from, tpl_to))

def generate(tuples):
	copy(tuples)

	sufixed = ['bookshelf', 'chest', 'trapped_chest', 'hedge', 'ladder', 'leaf_carpet', 'post', 'trapped_chest']
	afixed = [['stripped', 'post'], ['vertical', 'planks']]

	for obj in sufixed:
		fullcaps = obj.replace('_', ' ').title()
		localize((
			lambda name, modid: 'block.{modid}.{name}_{thing}'.format(name = name, modid = modid, thing = obj),
			lambda name, modid: localize_name(name, modid) + ' ' + fullcaps
		))

	for obj in afixed:
		fullcaps = [e.title() for e in obj]
		localize((
			lambda name, modid: 'block.{modid}.{thing0}_{name}_{thing1}'.format(name = name, modid = modid, thing0 = obj[0], thing1 = obj[1]),
			lambda name, modid: fullcaps[0] + ' ' + localize_name(name, modid) + ' ' + fullcaps[1]
		))

	import update_tags
	import update_drop_tables


main()

