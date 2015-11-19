/*@é&*/
$(document).ready( function () {

	var gbif_key = FloristicInterface.getSpeciesGbifKey();

	var osm_url = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osm_attrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osm_url, { attribution: osm_attrib });

	var map = new L.Map('map', {
		center: [0,0],
		zoom: 1,
		minZoom: 1,
		worldCopyJump: false,
		zoomControl: false
	});

	map.addLayer(osm);

	if (gbif_key && gbif_key != "") {
		var gbif_url = 'http://api.gbif.org/v1/map/density/tile?x={x}&y={y}&z={z}';
		gbif_url += '&type=TAXON&key=' + gbif_key + '&resolution=8';
		var gbif_attrib = '<a href="http://www.gbif.org">GBIF</a> contributors';
		var gbif = new L.TileLayer(gbif_url, { attribution: gbif_attrib });

		var base_maps = {};
		var overlay_maps = {
			'GBIF': gbif
		};

		map.addLayer(gbif);

		L.control.layers(base_maps, overlay_maps).addTo(map);
	}
});