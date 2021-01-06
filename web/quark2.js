let selectedEntry = "home";
let selectedCategory = "automation";
let transitioning = false;

$(function() {
	let changed = false;
	if(window.location.hash) {
		const target = window.location.hash.substring(1);
		if($(`.navbar-link[data-entry=${target}]`).length > 0 && target != selectedEntry) {
			selectedEntry = target;
			changed = true;
		}
	}
	const firstVersion = "1.14", lastVersion = "1.16.4";
	let currentVersion = new URLSearchParams(window.location.search.substring(1)).get("version");
	if (!currentVersion || !checkSemverBounds(currentVersion, firstVersion, lastVersion)) {
		currentVersion = lastVersion;
	}
	$('.feature-info').filter((i, elem) => !checkSemverRange(currentVersion, elem.querySelector('.feature-version').innerText, lastVersion)).addClass('removed-version');

	updateEntry(changed, false);
	updateCategory(false);
	updateBtt();

	const images = $('#big-branding-background img');
	shuffle(images);
	images.addClass('displayed-image');
});

$(window).scroll(updateBtt);

$('.data-entry-changer').click(function() {
	var newEntry = $(this).attr('data-entry')
	var changed = newEntry != selectedEntry;

	scrollTo(0, function() {
		selectedEntry = newEntry;
		updateEntry(changed, true);
	});
});

$('.data-category-changer').click(function() {
	var newCategory = $(this).attr('data-category')
	var changed = newCategory != selectedCategory;

	scrollTo($('#feature-category-strip').offset().top - 5, function() {
		selectedCategory = newCategory;
		updateCategory(changed);	
	});
});

$('.feature-expand-button').click(function() {
	flipFlopCollapsible($(this), $(this).parent().find('.feature-expand'), 'ᕕ( ᐛ )ᕗ', 'More Info', 'Less Info', '50px');
});

$('#install-instructions-button').click(function() {
	flipFlopCollapsible($(this), $('#install-instructions'), '', 'Show How to Install', 'Hide How to Install', 0);
});

function scrollTo(target, callback) {
	if(transitioning)
		return;

	var top = window.pageYOffset || document.documentElement.scrollTop;
	transitioning = true;

	$("html, body").animate({ scrollTop: target }, Math.min(Math.abs(top - target), 500), function() {
		transitioning = false;
		if(callback)
			callback();
	});
}

function flipFlopCollapsible(button, contents, textAnimating, textShow, textHide, targetMarginBottom) {
	if(contents.attr('data-animating') == 'true')
		return;

	var title = button.find('.button-title');
	if(textAnimating)
		title.text(textAnimating);

	if(contents.attr('data-expanded') == 'true') {
		contents.attr('data-animating', 'true');
		var height = contents.css('height');
		contents.animate({'height': 0, 'margin-bottom': 0}, 500, function() {
			title.text(textShow);
			contents.hide();
			contents.css('height', height);
			contents.attr('data-expanded', 'false');	
			contents.attr('data-animating', 'false');
		});
	} else {
		var height = contents.css('height');
		contents.css({'height': 0, 'margin-bottom': 0});

		contents.show();
		contents.attr('data-animating', 'true');
		contents.animate({'height': height, 'margin-bottom': targetMarginBottom}, 500, function() {
			title.text(textHide);
			contents.attr('data-expanded', 'true');
			contents.attr('data-animating', 'false');
		});
	}
}

$('#button-btt').click(function() {
	scrollTo(0, null);
});

function updateEntry(changed, setHash) {
	var dataSelector = `[data-entry=${selectedEntry}]`;

	$(`.navbar-link${dataSelector}`).addClass('navbar-selected');
	$(`.navbar-link:not(${dataSelector})`).removeClass('navbar-selected');

	if(changed) {
		if(selectedEntry == 'features')
			updateCategory(false);

		var next = $(`.content-holder${dataSelector}`);
		next.css({opacity: 0, 'margin-left': '150px'});

		$('.active-holder').animate({opacity: 0, 'margin-left': '-150px'}, 250, function() {
			$(this).removeClass('active-holder');	
			next.addClass('active-holder');
			next.animate({opacity: 1, 'margin-left': '0px'}, 250);
			updateLazyImages();
		});

		if(setHash)
			window.location.hash = `#${selectedEntry}`;
	}
}

function updateCategory(changed) {
	var dataSelector = `[data-category=${selectedCategory}]`;

	$(`.category-navbar-link${dataSelector}`).addClass('navbar-selected');
	$(`.category-navbar-link:not(${dataSelector})`).removeClass('navbar-selected');

	if(changed) {
		var next = $(`.feature-category${dataSelector}`);
		next.css({opacity: 0, 'margin-left': '150px'});

		$('.active-category').animate({opacity: 0, 'margin-left': '-150px'}, 250, function() {
			$(this).removeClass('active-category');	
			next.addClass('active-category');
			next.animate({opacity: 1, 'margin-left': '0px'}, 250);
			updateLazyImages();
		});
	}
}

function updateLazyImages() {
	$('.active-category img:not(.loaded-image)').each(function(e) {
		var elm = $(this);
		elm.attr('src', elm.attr('data-lazy-src'));
		elm.addClass('loaded-image');
	});
}

function updateBtt() {
	const top = window.pageYOffset || document.documentElement.scrollTop;
	const bttVisible = top > 200;
	$('#btt-holder').attr('data-enabled', bttVisible);
}

function compareSemver(l, r) {
	const leftArr = l.split('.'), rightArr = r.split('.');
	const rows = Math.min(leftArr.length, rightArr.length);
	for (let i = 0; i < rows; i++) {
		const difference = (+leftArr[i] || 0) - (+rightArr[i] || 0);
		if (difference != 0)
			return difference;
	}
	return leftArr.length - rightArr.length;
}

function checkSemverBounds(t, l, r) {
	return compareSemver(t, l) >= 0 && compareSemver(t, r) <= 0;
}

function checkSemverRange(current, rangeStr, last) {
	if (rangeStr.endsWith('+')) {
		return checkSemverBounds(current, rangeStr.slice(0, -1), last);
	} else {
		const array = rangeStr.split('-');
		return array.length == 1 || checkSemverBounds(current, array[0], array[1]);
	}
}

// https://stackoverflow.com/questions/6274339/how-can-i-shuffle-an-array
function shuffle(a) {
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}
