const versions = [ 12, 11, 10, 9 ];
var disabled = [];

$(function() {
	$('#header').css('background-image', 'url(img/backgrounds/' + Math.floor(Math.random() * 8) + '.jpg)');

	$.getJSON('features.json', loadFeatures); 
	
	$(document).on('click', '.feature-button', function() {
		var elm = $(this);
		var i = elm.find('i');
		var enabled = elm.hasClass('feature-button-enabled');
		var classname = elm.attr('data-classname');

		var setIcon = function(icon) {
			i.animate({
				opacity: 0
			}, 200, function() {
				i.text(icon);
				i.animate({
					opacity: 1
				}, 200);
			});
		};

		if(enabled) {
			elm.removeClass('feature-button-enabled');
			elm.addClass('feature-button-disabled');
			setIcon('clear');
			disabled.push(classname);
		} else {
			elm.removeClass('feature-button-disabled');
			elm.addClass('feature-button-enabled');
			setIcon('done');
			disabled.splice(disabled.indexOf(classname), 1);
		}

		var callback = function() {
			$('#disabled-feature-count').text(count);
			$('#disabled-feature-plural').text(count == 1 ? '' : 's');
		};

		var count = disabled.length;
		if(count > 0) {
			$('#import-string-container').animate({ 'margin-top': '0px' }, 200);
			callback();
		} else $('#import-string-container').animate({ 'margin-top': '100px' }, 200, callback);

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

$('#import-string-button').click(function() {
	copyToClipboard(disabled.toString());
});

function loadFeatures(obj) {
	loadTemplate('module', function(data) {
		for(module in obj) {
			var moduleData = obj[module];
			moduleData.module_key = module;

			for(i in moduleData.features) {
				var feature = moduleData.features[i];

				var versionData = [];
				var first = feature.introduced;
				var last = feature.added_to_vanilla;

				feature.id = feature.name.toLowerCase().replace(/\s/g, '-');
				feature.has_album = feature.album != null;
				feature.was_contributed = feature.contributor != null;
				feature.is_in_vanilla = last != null;
				feature.anchor = encodeURIComponent(module + '-' + feature.id);
				feature.disable_counter = feature.disable_counter || feature.is_in_vanilla || moduleData.disable_counter;

				if(last != null)
					feature.implement_version = "Minecraft 1." + last;

				for(j in versions) {
					var ver = versions[j];
					versionData.push({
						'name': '1.' + ver,
						'enabled': ((ver >= first) && (last == null || ver < last))
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

		$('#feature-counter').html($(document).find('.feature-card:not(.disable-counter)').length);
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

function copyToClipboard(str) {
  var area = document.createElement("textarea");
  area.value = str;

  $('body').append(area);
  area.select();

  try {
    document.execCommand('copy');
    alert('Copied to clipboard!');
  } catch (err) { }

  $('body').remove(textArea);
}