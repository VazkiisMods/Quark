const versions = [ 11, 10, 9 ];

$(function() {
	$('#header').css('background-image', 'url(img/backgrounds/' + Math.floor(Math.random() * 8) + '.jpg)');

	$.getJSON('features.json', loadFeatures); 
	
	$(document).on('click', '.hyperlink-button', function() {
		scrollTo($(this), 110);
	});
});

$('#btt-button').click(function() {
	scrollToPos(0);
});

$('.module-button').click(function() {
	$('#module-' + $(this).attr('data-module')).find('.lazyload-image').each(function(i) {
		$(this).trigger('openmodule');
	});
});

function loadFeatures(obj) {
	loadTemplate('module', function(data) {
		for(module in obj) {
			var moduleData = obj[module];
			moduleData.module_key = module;

			for(i in moduleData.features) {
				feature = moduleData.features[i];
				feature.id = feature.name.toLowerCase().replace(/\s/g, '-');
				feature.has_album = feature.album != null;
				feature.was_contributed = feature.contributor != null;
				feature.anchor = encodeURIComponent(module + '-' + feature.id);

				var versionData = [];
				var first = feature.introduced;
				for(j in versions) {
					var ver = versions[j];
					versionData.push({
						'name': '1.' + ver,
						'enabled': (ver >= first)
					});
				}

				feature.versions = versionData;
			}

			moduleData.features.sort(function(a, b) {
				return a.name.localeCompare(b.name);
			})

			var id = '#module-' + module;
			$(id).html(Mustache.to_html(data, moduleData));
		}

		$('#feature-counter').html($(document).find('.feature-card').length);
		$(document).find('.lazyload-image').each(function(i) {
			$(this).lazyload({
				event: 'openmodule',
				effect: 'fadeIn'
			});
		});

		scrollToHash();
	});
}

function loadTemplate(name, callback) {
	var templateData = '';

	$.ajax({
		url: 'templates/' + name + '.html',
		success: callback
	});
}

function scrollToHash() {
	var hash = window.location.hash.substr(1);
	var dash = hash.indexOf('-');
	var hashModule = hash.substr(0, dash);
	var hashFeature = hash.substr(dash + 1);
	
	$('a[data-module=' + hashModule + ']').find('span').click();
	scrollTo($('#' + hash + '-fake'), 360);
}

function scrollTo(element, off) {
	var top = 0;
	if(element != null)
		top = element.position().top + off;

	scrollToPos(top);
}

function scrollToPos(pos) {
	$('html, body').animate({
		scrollTop: pos
	}, 600);
}