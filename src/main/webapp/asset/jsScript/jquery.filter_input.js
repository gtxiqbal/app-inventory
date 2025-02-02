/*

  Author - Rudolf Naprstek
  Website - http://www.thimbleopensource.com/tutorials-snippets/jquery-plugin-filter-text-input
  Version - 1.3.0
  Release - 28th January 2012

  Thanks to Niko Halink from ARGH!media for bugfix!
 
  Remy Blom: Added a callback function when the filter surpresses a keypress in order to give user feedback

 */

(function($) {

	$.fn.extend({

		filter_input : function(options) {

			var defaults = {
				regex : ".*",
				live : false
			}

			var options = $.extend(defaults, options);
			var regex = new RegExp(options.regex);

			function filter_input_function(event) {

				var key = event.charCode ? event.charCode
						: event.keyCode ? event.keyCode : 0;
				
				//alert("The CTRL key was pressed!="+event.ctrlKey+" key="+key);
				if (event.ctrlKey==true && (key == 99 || key == 118)) {
					return true;
				}
				// 8 = backspace, 9 = tab, 13 = enter, 35 = end, 36 = home, 37 =
				// left, 39 = right, 46 = delete
				if (key == 8 || key == 9 || key == 13 || key == 35 || key == 36
						|| key == 37 || key == 39 || key == 46) {
					if ($.browser.mozilla) {

						// if charCode = key & keyCode = 0
						// 35 = #, 36 = $, 37 = %, 39 = ', 46 = .

						if (event.charCode == 0 && event.keyCode == key) {
							return true;
						}

					}
				}

				var string = String.fromCharCode(key);
				if (regex.test(string)) {
					return true;
				} else if (typeof (options.feedback) == 'function') {
					options.feedback.call(this, string);
				}
				return false;
			}

			if (options.live) {
				$(this).live('keypress', filter_input_function);
			} else {
				return this.each(function() {
					var input = $(this);
					input.unbind('keypress').keypress(filter_input_function);
				});
			}

		}
	});

})(jQuery);
