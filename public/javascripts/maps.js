var map;
var marker;
var polygon;
var polygon2;

function initialize() {
	var myOptions  = {
		center: new google.maps.LatLng(40.818488, -73.937637),
		zoom: 15,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
};

$(function() {
	$("#submit").click(function(e) {
		e.preventDefault(); 
		if (marker)
		marker.setMap(null);
		if(polygon)
		polygon.setMap(null);
		if(polygon2)
		polygon2.setMap(null);

		var address = $("#address").val();
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode( { 'address': address}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var latLng = results[0].geometry.location;
				map.setCenter(latLng);
				marker = new google.maps.Marker({
					map: map,
					position: latLng
				});

				var myCoordinates = [
				new google.maps.LatLng(40.818488,-73.937637),
				new google.maps.LatLng(40.820891,-73.943302),
				new google.maps.LatLng(40.820274,-73.943774),
				new google.maps.LatLng(40.820859,-73.945105),
				new google.maps.LatLng(40.817091,-73.948023),
				new google.maps.LatLng(40.815378,-73.948785),
				new google.maps.LatLng(40.814055,-73.949632),
				new google.maps.LatLng(40.812634,-73.950823),
				new google.maps.LatLng(40.811472,-73.952014),
				new google.maps.LatLng(40.811424,-73.952078),
				new google.maps.LatLng(40.808419,-73.944976)
				];
				var polyOptions = {
					path: myCoordinates,
					strokeColor: "#0000FF",
					strokeOpacity: 0.8,
					strokeWeight: 3,
					fillColor: "#0000FF",
					fillOpacity: 0.2
				}
				polygon = new google.maps.Polygon(polyOptions);

				var myCoordinates2 = [
				new google.maps.LatLng(40.731326,-73.982570),
				new google.maps.LatLng(40.723033,-73.988664),
				new google.maps.LatLng(40.719292,-73.990295),
				new google.maps.LatLng(40.718446,-73.988235),
				new google.maps.LatLng(40.720723,-73.981325),
				new google.maps.LatLng(40.728431,-73.975703)
				];
				var polyOptions2 = {
					path: myCoordinates2,
					strokeColor: "#FF0000",
					strokeOpacity: 0.8,
					strokeWeight: 3,
					fillColor: "#FF0000",
					fillOpacity: 0.2
				}
				polygon2 = new google.maps.Polygon(polyOptions2);

				if(google.maps.geometry.poly.containsLocation(latLng, polygon))
				polygon.setMap(map);
				if(google.maps.geometry.poly.containsLocation(latLng, polygon2))
				polygon2.setMap(map);
			} else {
				alert("Geocode failed: " + status);
			}
		});
	});  
});