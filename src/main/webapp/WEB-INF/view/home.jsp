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
        <option value="kiev">Kiev</option>
        <option value="melitopol">Melitopol</option>
    </select>
    <b>End: </b>
    <select id="end" onchange="calcRoute();">
        <option value="melitopol">Melitopol</option>
        <option value="kiev">Kiev</option>
    </select>
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
        document.getElementById('start').addEventListener('change', onChangeHandler);
        document.getElementById('end').addEventListener('change', onChangeHandler);
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

</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBIEtFvEUP60aSI4AjwQAbW7TG5xlEburY&signed_in=true&callback=initMap" async defer>

</script>
</body>
</html>
