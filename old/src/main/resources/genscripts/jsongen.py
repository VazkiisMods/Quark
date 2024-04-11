import sys, os, re

modid = 'quark'
params = {}

def copy_template(name, base, target):
	params['modid'] = modid;
	params['name'] = name

	base_file = 'templates/{0}'.format(base)
	target_file = '../{0}'.format(target.format(**params))

	target_dir = re.sub(r'/[^/]+?$', '', target_file)
	if not os.path.exists(target_dir):
		os.makedirs(target_dir)

	with open(base_file, 'r') as reader:
		with open(target_file, 'w') as writer:
			for line in reader:
				for param in params:
					line = line.replace('%{0}%'.format(param), params[param])

				writer.write(line)

def copy(templates):
	foreach_arg_array(1, sys.argv, templates, copy_callback)

def copy_callback(name, templates):
	if '=' in name:
		parse_param(name)
	else:
		for tup in templates:
			base = tup[0]
			target = tup[1]
			copy_template(name, base, target)

def localize_standard(prefix):
	localize((
		lambda name, modid: '{prefix}.{modid}.{name}'.format(prefix = prefix, name = name, modid = modid),
		localize_name
	))

def localize_name(name, modid):
	return ' '.join(map(lambda s: s.capitalize(), name.split('_')))

def localize(func):
	foreach_arg_array(1, sys.argv, func, localize_callback)

def localize_callback(name, funcs):
	if not '=' in name:
		key = funcs[0](name, modid)
		val = funcs[1](name, modid)
		print('"{0}": "{1}",'.format(key, val))

def foreach_arg_file(file, templates, func):
	lines = []
	with open(file, 'r') as reader:
		for line in reader:
			lines.append(line.strip())

	foreach_arg_array(0, lines, templates, func)

def foreach_arg_array(start, arr, templates, func):
	argslen = len(arr)
	for i in range(start, argslen):
		name = arr[i]
		if name.startswith('file:'):
			foreach_arg_file(name[5:], templates, func)
		else:
			func(name, templates)

def parse_param(str):
	toks = str.split('=')
	params[toks[0]] = toks[1]
