<!DOCTYPE html>
<html>
	<head>
		<title>Quark</title>

		<meta name="theme-color" content="#48ddbc">
		
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/material-design-lite/1.3.0/material.teal-orange.min.css">
		<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">

		<meta name="twitter:card" content="summary_large_image" />
		<meta name="twitter:description" content="A Minecraft Mod staying in the theme of Vanilla." />
		<meta name="twitter:title" content="Quark" />
		<meta name="twitter:site" content="@vazkii" />

		<link rel="stylesheet" href="quark.css">
		<link rel="icon" href="favicon.ico">
	</head>

	<body>
		<div id="main-container">
			<div id="header">
				<img src="img/icon.png" id="quark-logo"></img>
				<a href="https://www.creeperhost.net/"><img id="sellout-image" src="http://vazkii.us/sellout.png"></img></a>

				<div id="header-contents">
					<div class="title"><span class="q">q</span>uark</div>
					<div class="subtitle">Change your game. Not your gameplay.</div>
				</div>

				<a href="http://minecraft.curseforge.com/projects/quark"><button id="download-button" class="mdl-button mdl-js-button mdl-button--fab mdl-button--colored">
					<i class="material-icons">file_download</i>
				</button></a>

				<div id="download-section">
					<img src="http://cf.way2muchnoise.eu/full_quark_downloads.svg"></img> <img src="http://cf.way2muchnoise.eu/versions/quark.svg"></img>
				</div>
			</div>

			<div id="contents" class="mdl-layout mdl-js-layout mdl-layout--transparent">
				<header class="mdl-layout__header mdl-layout__header--transparent">
					<div class="mdl-layout__tab-bar mdl-js-ripple-effect mdl-layout--fixed-tabs mdl-layout__header--transparent">
						<a href="#module-info" data-module="info" class="mdl-layout__tab module-button is-active">Info</a>
						<a href="#module-automation" data-module="automation" class="mdl-layout__tab module-button">Automation</a>
						<a href="#module-building" data-module="building" class="mdl-layout__tab module-button">Building</a>
						<a href="#module-decoration" data-module="decoration" class="mdl-layout__tab module-button">Decoration</a>
						<a href="#module-management" data-module="management" class="mdl-layout__tab module-button">Management</a>
						<a href="#module-tweaks" data-module="tweaks" class="mdl-layout__tab module-button">Tweaks</a>
						<a href="#module-vanity" data-module="vanity" class="mdl-layout__tab module-button">Vanity</a>
						<a href="#module-world" data-module="world" class="mdl-layout__tab module-button">World</a>
						<a href="#module-misc" data-module="misc" class="mdl-layout__tab module-button">Misc</a>
					</div>
				</header>

				<div class="mdl-layout__content" id="content-holder">
					<section class="mdl-layout__tab-panel is-active card info-card" id="module-info">
						<div class="page-content">
							<b>Quark</b> is a mod for Minecraft. It aims to create an experience akin to the "vanilla" experience, by having a very simple motto: <i>anything that would be added to Quark could also be added to the default game without compromising its gameplay style.</i> Quark currently has <b id="feature-counter"></b> features to spice up your game, all of which fitting the previous motto.
							<br><br>
							To that end, every feature added to Quark is simple and small. Hence the name of the mod, Quark, as a Quark is a very small thing. Quark is a <i>Modular</i> mod. Which means that its features are split within various modules. All of the modules can be disabled, and all the features can also be disabled individually if one wants to. Many of the features even have extra configuration options.
							<br><br>
							The mod features ingame configuration via the Mod Options menu. Most features can be toggled from inside the game, others require a world reload, it depends on the changes they make. Any features that add new explicit content, such as items, blocks or entities, will require a reset.
							<br><br>
							Check out the various tabs to have a look of what the mod contains. If you're on mobile you can scroll left on the tabs to see more. You can download Quark using the download button in the header. Scroll down a bit for installation instructions and other stuff.
							<br><br>
							<b>Note that this mod requires <a href="https://minecraft.curseforge.com/projects/autoreglib">AutoRegLib</a>, it must be installed for it to work.</b> (older versions of the mod may not require it)
							<br><br>
							<img id="main-image" src="img/main.png"></img>

							<hr>

							<b>Installation Instructions</b>
							<br><br>
							This mod requires Minecraft Forge. We do not support Bukkit, Spigot, Cauldron, Sponge or any other alternative servers. Though some <i>may</i> be compatible, if you choose to go that way, you're on your own. <i>The mod is provided as-is, with no warranty. We are not responsible if it breaks or destroys your save. Always make backups.</i>
							<br><br>
							This is just a standard forge mod installation. If you've done it before you can skip this.
							<ol>
								<li>Make sure you have Minecraft installed.</li>
								<li>Locate the version of Minecraft Forge for your target Minecraft version <a href="http://files.minecraftforge.net/">here</a>.</li>
								<li>Download installer-win (or installer if you're not on Windows) and run it. A new forge profile will be added to your minecraft launcher.</li>
								<li>Download AutoRegLib using the link above the image. Put the downloaded .jar into your "mods" folder.</li>
								<li>Download Quark using the download button above. Put the downloaded .jar file into your "mods" folder.
									<ul>
										<li>The "mods" folder is next to your "resourcepacks" folder. To locate it, run the new forge instance, open the Resource Packs menu, open the folder, and go one level up to where all your minecraft files are. The "mods" folder should be there alongside others like "screenshots" or "saves".</li></ul></li>
								<li>Run your new forge profile with Quark installed.</li>
							</ol>

							For server installation:
							<ol>
								<li>Follow the above steps, up to step 3. For step 3 pick Server rather than Client, and change the location to an empty folder somewhere.</li>
								<li>Create a "mods" folder inside the empty folder you picked, that now contains your server files.</li>
								<li>Download AutoRegLib using the link above the image. Put the downloaded .jar into that "mods" folder.</li>
								<li>Download Quark using the download button above. Put the downloaded .jar file into that "mods" folder.</li>
								<li>Run the server using the forge jar. <i>Not the minecraft_server jar</i></li>
							</ol>

							Alternatively, you can use the <a href="https://www.curse.com/download">Curse app</a> and install the mod directly from inside it. Make sure you have Minecraft installed so it installs the Minecraft plugin.<br>
							Yes, you can use the mod in modpacks.
							<hr>
							<b>Credits</b>
							<ul>
								<li>wiiv for most of the textures.</li>
								<li>Rorax for the emote icons.</li>
								<li>DylanKaiser for the inventory chest icon.</li>
								<li>SanAndreasP for the chest textures and most of their code.</li>
								<li>Darkhax for the sheep having wool feature.</li>
								<li>ZeroLevels for the Iron Plate texture.</li>
								<li>Noodlor for the variety dungeon structures.</li>
								<li>Xisumavoid for the sticky piston side texture.</li>
								<li>/u/Soniop for the realistic world preset.</li>
								<li>/u/Martwaza for the trapdoor textures.</li>
								<li>/u/FelitonC and /u/origamidragon412 for the banner textures.</li>
								<li>/u/kopasz7 for the Midori block textures.</li>
								<li>/u/robotthunder500 for the Bookshelf textures.</li>
								<li>/u/darwinpatrick for the Soul Sandstone textures.</li>
								<li>/u/Nyodex for the original Duskbound Block idea and textures.</li>
								<li>/u/MushirMickeyJoe for recreation of the pocket edition piston model.</li>
								<li>/u/CopherSans for the improved bow animation.</li>
								<li>The creators of all the awesome suggestions I found in /r/MinecraftSuggestions that I implemented here.</li>
							</ul>
							<hr>
							<b>Contact and Donations</b>
							<ul>
								<li><a href="https://www.reddit.com/r/quarkmod">Quark Subreddit</a> <i>(suggestions should go here, make sure to read the sticky!)</i></li>
								<li><a href="https://github.com/Vazkii/Quark/issues">Issues</a> <i>(report your bugs here)</i></li>
								<li><a href="https://github.com/Vazkii/Quark">Source Code</a></li>
								<li><a href="https://github.com/Vazkii/Quark/blob/master/modders.md">APIs for Modders</a></li>
								<li><a href="https://twitter.com/Vazkii/">Vazkii's Twitter</a></li>
								<li><a href="https://patreon.com/Vazkii/">Vazkii's Patreon</a></li>
								<li><a href="https://twitter.com/wiliv75/">wiiv's Twitter</a></li>
								<li><a href="https://patreon.com/WIIV/">wiiv's Patreon</a></li>
								<li><a href="http://vazkii.us/about-me/supportdonations/">Donations</a></li>
								<li><a href="http://vazkii.us/about-me/contact/">Email and other Contacts</a></li>
								<li><a href="http://vazkii.us/">vazkii.us</a></li>
							</ul>
							<br>
							</div>
					</section>

					<section class="mdl-layout__tab-panel" id="module-automation"></section>
					<section class="mdl-layout__tab-panel" id="module-building"></section>
					<section class="mdl-layout__tab-panel" id="module-decoration"></section>
					<section class="mdl-layout__tab-panel" id="module-management"></section>
					<section class="mdl-layout__tab-panel" id="module-tweaks"></section>
					<section class="mdl-layout__tab-panel" id="module-vanity"></section>
					<section class="mdl-layout__tab-panel" id="module-world"></section>
					<section class="mdl-layout__tab-panel" id="module-misc"></section>
				</div>
			</div>
		</div>

		<button id="btt-button" class="mdl-button mdl-js-button mdl-button--fab mdl-button--colored">
			<i class="material-icons">arrow_upward</i>
		</button>

		<div id="footer">
			copyright lololol
		</div>

		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.lazyload/1.9.1/jquery.lazyload.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/mustache.js/2.1.3/mustache.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/material-design-lite/1.3.0/material.min.js"></script>
		<script src="quark.js"></script>
	</body>
</html>

