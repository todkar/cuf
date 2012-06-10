var map;
var marker;
var polygons = {};

function initialize() {
    for (var i in data.creditUnions) {
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
            fillOpacity:0.1
        }
        var polygon = new google.maps.Polygon(polyOptions);
        polygons[creditUnion.id] = polygon;
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

                var youAreHere = "<b>You are here.</b>";
                var toolTip = youAreHere + "<p>Looks like you're not eligible for any credit unions at the moment. :(</p>"
                for (var i in polygons) {
                    var polygon = polygons[i];
                    polygon.setMap(null);
                    if (google.maps.geometry.poly.containsLocation(latLng, polygon)) {
                        polygon.setMap(map);

                        var creditUnion = data.creditUnions[i];
                        var eligible = "<p>You are eligible for:<br /><a href='" + creditUnion.www +
                            "' target='_blank'>" + creditUnion.name + "</a></p>"
                        toolTip = youAreHere + eligible;
                    }
                }
                var infoWindow = new google.maps.InfoWindow({
                    content: toolTip
                });
                infoWindow.open(map, marker);
            } else {
                alert("Geocode failed: " + status);
            }
        });
    });
});