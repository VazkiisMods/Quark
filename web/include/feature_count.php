<?php
	function main_feature_count() {
		global $feature_data;
		
		$count = 0;
		foreach($feature_data as $k => $module)
			foreach($module as $k => $feature) {
				if(array_key_exists('addon', $feature) && $feature['addon'])
					break;

				if(!array_key_exists('removed', $feature) || !$feature['removed'])
					$count++;
			}

		echo $count;
	}

	main_feature_count();
?>