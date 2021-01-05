<?php
	$query_string = $_SERVER['QUERY_STRING'] ?: "";
	$first_version = "1.14";
	$latest_version = "1.16.4"; # NOTE: This needs to be updated when Quark changes versions!
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
