<?php
	parse_str($query_string, $query);
	$current_version = $query["version"];
	if (!$current_version || !version_valid($current_version, $first_version, $latest_version)) {
		$current_version = $latest_version;
	}

	function main_features() {
		global $feature_data;
		write_feature_data($feature_data);
	}
	
	function write_feature_data($data) {
		$first = true;
		$categories = array_keys($data);

		foreach($categories as $i => $key) {
			$category = $data[$key];
			$next = $i >= (sizeof($categories) - 1) ? "" : $categories[$i + 1];
			write_category($key, $category, $first, $next);
			$first = false;
		}
	}

	function write_category($name, $category, $displayed, $next) {
		$class = 'feature-category';
		if($displayed)
			$class = "$class active-category";

		div($class, array('data-category' => $name ));
			div('section-header');
				write(ucfirst($name));
			pop();
			$count = sizeof($category);

			$hide_count = false;
			if($count > 0) {
				$first = $category[0];
				if(array_key_exists('addon', $first) && $first['addon'])
					$count--;

				if(array_key_exists('hide_count', $first) && $first['hide_count'])
					$hide_count = true;
			}

			if(!$hide_count) {
				div('feature-count');
					write("($count Features)");
				pop();
			}

			if($count == 0) {
				push('h1');
					write('No features here yet!');
				pop();
			} else {
				div('feature-list');
					usort($category, "cmp_features");
					foreach($category as $k => $feature)
						write_feature($feature, $name);
				pop();
			}

			if(strlen($next)) {
				div('data-category-changer std-button button-long button-next button-features', array('data-category' => $next));
					div('button-title');
						write('Next: ' . ucfirst($next));
					pop();
				pop();	
			} else {
				div('data-entry-changer std-button button-long button-next button-features', array('data-entry' => 'download'));
					div('button-title');
						write('Download Quark');
					pop();
				pop();	
			}
		pop();
	}

	function write_feature($feature, $category_name) {
		global $current_version, $latest_version;
		if(array_key_exists('info', $feature)) {
			div('info');
				write($feature['info']);
			pop();
			return;
		}

		$version_data = $feature['versions'];
		if (is_string($version_data)) {
			// Is this a Quark Friend or something? If so just call it valid lol
			$version_string = $version_data;
			$version_expired = false;
		} else {
			// Assume this is an assoc array
			$first_version = $version_data['start'];
			$last_version = $version_data['end'] ?: $latest_version;

			$version_string = "$first_version-$last_version";
			$version_expired = !version_valid($current_version, $first_version, $last_version);
		}
		div('feature');
			div('feature-image');
				img("img/features/$category_name/{$feature['image']}");
			pop();

			div($version_expired ? 'feature-info removed-version' : 'feature-info');
				div('feature-header');
					div('feature-title');
						write($feature['name']);
						if($version_expired) {
							span('feature-removed');
								write(' (Unavailable)');
							pop();
						}
					pop();

					div('feature-version');
						write($version_string);
					pop();
				pop();

				$has_expand = array_key_exists('expand', $feature);

				div('feature-desc' . ($has_expand ? ' feature-desc-with-expand' : ''));
					write_desc($feature['desc']);
				pop();

				if($has_expand) {
					div('feature-expand');
						write_desc($feature['expand']);
					pop();

					div('std-button feature-expand-button');
						div('button-title');
							write('More Info');
						pop();
					pop();
				} else {
					$has_download = array_key_exists('download', $feature);

					if($has_download) {
						a($feature['download'], 'no-external friend-dl-container');
							div('std-button button-download button-download-friend');
								div('button-title');
									write('Download ');
								pop();
							pop();
						pop();
					}
				}
			pop();
		pop();
	}

	function write_desc($arr) {
		$list = false;
		foreach($arr as $i => $line) {
			$fchar = $line[0];
			$rem = substr($line, 1);

			if($fchar == '*') {
				if(!$list)
					push('ul');

				$list = true;
				push('li');
					write($rem);
				pop();
			} else {
				if($list) {
					pop();
					$list = false;
				}

				switch ($fchar) {
					case '#':
						div('feature-desc-header');
							write($rem);
						pop();
						break;
					case '!':
						img($rem);
						break;
					case '-':
						write('<hr>');
						break;
					default:
						p($line);
						break;
				}
			}
		}

		if($list)
			pop();
	}

	function cmp_features($f1, $f2) {
		if((array_key_exists('addon', $f1) && $f1['addon']))
			return -1;	
		if((array_key_exists('addon', $f2) && $f2['addon']))
			return 1;	

		return strcmp($f1['name'], $f2['name']);
	}

	main_features();
?>
