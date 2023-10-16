export def fix-cd [] {
	if ("zeta_progress.nu" | path exists) {
		cd ..
	}
}

export def report [] {
	fix-cd

	let alljava = ls src/**/*.java | select name size | sort-by --reverse size
	print $"Total classes: ($alljava | length)"

	let problematic = $alljava |
		insert forge { $in.name | open --raw | str contains "net.minecraftforge" } |
		insert arl { $in.name | open --raw | str contains "vazkii.arl" } |
		filter { $in.forge or $in.arl }

	print $problematic
	print $"Classes touching Forge: ($problematic | filter { $in.forge } | length)"
	print $"Classes touching ARL: ($problematic | filter { $in.arl } | length)"
}

export def main [subcmd: string = "report"] {
	match $subcmd {
		"report" => report
	}
}