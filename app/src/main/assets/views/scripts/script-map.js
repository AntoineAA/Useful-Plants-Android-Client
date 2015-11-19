/*@é&*/
$(document).ready( function () {
	
	var lat = parseFloat(FloristicInterface.getLatitude());
	var lng = parseFloat(FloristicInterface.getLongitude());

	var str_current_location = FloristicInterface.getCurrentLocationString();
	var str_detail = FloristicInterface.getDetailString();

	var geoJsonUrl = FloristicInterface.getGeoJsonUrl();

	var osm_url = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osm_attrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osm_url, { attribution: osm_attrib });

	var map = new L.Map('map', {
		center: [lat, lng],
		zoom: 13,
		minZoom: 1,
		worldCopyJump: false,
		zoomControl: false
	});

	map.addLayer(osm);

	var RedIcon = L.Icon.Default.extend({
		options: {
			iconUrl: './scripts/leaflet-0.7.3/images/marker-icon-red.png',
			shadowSize: [0, 0]
		}
	});
	var red_icon = new RedIcon();

	var NoShadowIcon = L.Icon.Default.extend({
		options: {
			iconUrl: './scripts/leaflet-0.7.3/images/marker-icon.png',
			shadowSize: [0, 0]
		}
	});

	var marker = L.marker([lat, lng], { icon: red_icon });
	marker.bindPopup(str_current_location);
	marker.addTo(map);

	var base_maps = {};
	var overlay_maps = {};

	$.ajax({
		url: geoJsonUrl,
		dataType: 'json',
		success: function (response) {
			if (response && (response instanceof Array) && response.length > 0) {
				var layers = {};

				var group = L.geoJson(response, {
					onEachFeature: function (feature, layer) {
						layer.bindPopup(
							'<b>' + feature.properties.name + '<b>'
							+ '<br />'
							+ '<a href="#' + feature.properties.id + '" class="poplink">' + str_detail + '</a>'
						);

						if (!(feature.properties.source in layers)) {
							layers[feature.properties.source] = [];
						}
						layers[feature.properties.source].push(layer);
					},
					pointToLayer: function (feature, latlng) {
						var icon = new NoShadowIcon();
						return L.marker(latlng, { icon: icon });
					}
				});

				for (key in layers) {
					var markers = L.markerClusterGroup({ showCoverageOnHover: false });
					markers.addLayer(L.layerGroup(layers[key]));
					overlay_maps[key] = markers;
					markers.addTo(map);
				}

				//map.fitBounds(markers.getBounds());
				FloristicInterface.onLoadEnd();

				L.control.layers(base_maps, overlay_maps).addTo(map);
			}
		}
	});

	$(document).on('click', '.poplink', function () {
		var id = $(this).attr('href').substring(1);
		FloristicInterface.displayDetail(id);
	});
});