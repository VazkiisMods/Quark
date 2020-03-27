<?php
	$feature_data = load_json('features.json');

	$elm_stack = [];

	function load_json($file) {
		$contents = file_get_contents($file);
		return json_decode($contents, true);
	}

	function img($src, $class="") {
		push('img', $class, array('src' => 'img/loading.jpg', 'data-lazy-src' => $src));
		pop();
	}

	function p($txt) {
		push('p');
			write($txt);
		pop();
	}

	function a($href, $class="") {
		push('a', $class, array('href' => $href));
	}

	function div($class="", $attrs=array()) {
		push('div', $class, $attrs);
	}

	function span($class="", $attrs=array()) {
		push('span', $class, $attrs);
	}

	function push($type, $class="", $attrs=array()) {
		global $elm_stack;

		$attr_str = "";
		if(strlen($class) > 0)
			$attr_str = "class='$class'";

		foreach($attrs as $key => $value)
			$attr_str = "$key='$value' $attr_str";
		
		write("<$type $attr_str>");
		array_push($elm_stack, $type);
	}

	function pop($count = 1) {
		global $elm_stack;
		for($i = 0; $i < $count; $i++) {
			$type = array_pop($elm_stack);
			write("</$type>");	
		}
	}

	function write($txt) {
		echo $txt;
	}

?>