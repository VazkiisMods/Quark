<?php
	$cache_enabled = false;
	$cache_file = 'cached.php';
	$cache_expiry_time = 86400; // 24h

	if(!$cache_enabled) {
		include 'include/main.php';
		exit();
	}
	
	if(file_exists($cache_file) && time() - $cache_expiry_time < filemtime($cache_file)) {
		include $cache_file;
		exit();
	}

	ob_start();
	include 'include/main.php';

	$handle = fopen($cache_file, 'w');
	fwrite($handle, ob_get_contents());
	fclose($handle);
	ob_end_flush();
?>