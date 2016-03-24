<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <link href="<c:url value="/resources/css/main.css"/>" rel="stylesheet">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" charset="utf-8">
    <title>GPS Tracker</title>
</head>
<body>
<div id="floating-panel">
    <b>Start: </b>
    <select id="start" onchange="calcRoute();">
        <option value="50.398056 30.633333">m.Poznyaki</option>
    </select>
    <b>End: </b>
    <select id="end" onchange="calcRoute();">
        <option value="50.501111 30.498056">m.Obolon</option>
    </select>
    <button id="draw" value="Draw"></button>
</div>
<div id="map"></div>
<script>
    function initMap() {
        var directionsService = new google.maps.DirectionsService;
        var directionsDisplay = new google.maps.DirectionsRenderer;
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 12,
            center: {lat: 50.45, lng: 30.5236}
        });
        directionsDisplay.setMap(map);

        var onChangeHandler = function() {
            calculateAndDisplayRoute(directionsService, directionsDisplay);
        };
        document.getElementById('draw').addEventListener('click', onChangeHandler);
    }

    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
        directionsService.route({
            origin: document.getElementById('start').value,
            destination: document.getElementById('end').value,
            travelMode: google.maps.TravelMode.DRIVING
        }, function(response, status) {
            if (status === google.maps.DirectionsStatus.OK) {
                directionsDisplay.setDirections(response);
            } else {
                window.alert('Directions request failed due to ' + status);
            }
        });
    }

    function initMap2() {
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 3,
            center: {lat: 0, lng: -180},
            mapTypeId: google.maps.MapTypeId.TERRAIN
        });

        var flightPlanCoordinates = [
            {lat: 37.772, lng: -122.214},
            {lat: 21.291, lng: -157.821},
            {lat: -18.142, lng: 178.431},
            {lat: -27.467, lng: 153.027}
        ];
        var flightPath = new google.maps.Polyline({
            path: flightPlanCoordinates,
            geodesic: true,
            strokeColor: '#FF0000',
            strokeOpacity: 1.0,
            strokeWeight: 2
        });

        flightPath.setMap(map);
    }

</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBIEtFvEUP60aSI4AjwQAbW7TG5xlEburY&signed_in=true&callback=initMap2" async defer>

</script>
</body>
</html>
