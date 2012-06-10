var map;
var marker;
var polygons = [];

function initialize() {
    for (var i = 0; i < data.creditUnions.length; ++i) {
        var creditUnion = data.creditUnions[i];
        var coords = creditUnion.coords.map(function (c) {
            return new google.maps.LatLng(c.lat, c.lon);
        });
        var polyOptions = {
            path:coords,
            strokeColor:"#0000FF",
            strokeOpacity:0.8,
            strokeWeight:3,
            fillColor:"#0000FF",
            fillOpacity:0.2
        }
        var polygon = new google.maps.Polygon(polyOptions);
        polygons.push(polygon);
    }

    var myOptions = {
        center:new google.maps.LatLng(40.772222, -73.949661),
        zoom:12,
        mapTypeId:google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
}
;

$(function () {
    $("#submit").click(function (e) {
        e.preventDefault();
        if (marker) {
            marker.setMap(null);
        }

        var address = $("#address").val();
        var geocoder = new google.maps.Geocoder();
        geocoder.geocode({ 'address':address }, function (results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                var latLng = results[0].geometry.location;
                map.setCenter(latLng);
                map.setZoom(15);
                marker = new google.maps.Marker({
                    map:map,
                    position:latLng
                });

                for (var i = 0; i < polygons.length; ++i) {
                    var polygon = polygons[i];
                    polygon.setMap(null);
                    if (google.maps.geometry.poly.containsLocation(latLng, polygon)) {
                        polygon.setMap(map);
                    }
                }
            } else {
                alert("Geocode failed: " + status);
            }
        });
    });
});