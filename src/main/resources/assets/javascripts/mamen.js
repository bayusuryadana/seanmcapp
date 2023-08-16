window.initMap = initMap;

let map;
var openedInfoWindow = null;

function initMap() {
    // Default to Jakarta
    const defaultLatLng = {
        lat: -6.172018,
        lng: 106.801848
    };

    map = new google.maps.Map(document.getElementById("map"), {
        zoom: 13,
        center: defaultLatLng,
    });

    renderMap();
}

function renderMap() {
    google.maps.event.addListener(map, "click", function(event) {
        if (openedInfoWindow) openedInfoWindow.close()
    });

    google.maps.event.addListener(map, "idle", function() {
        const nw = {
            lat: map.getBounds().getNorthEast().lat(),
            lng: map.getBounds().getNorthEast().lng()
        }
        const se = {
            lat: map.getBounds().getSouthWest().lat(),
            lng: map.getBounds().getSouthWest().lng()
        }

        fetchStalls(nw, se);
    });
}

async function fetchStalls(nw, se) {
    fetch('/api/mamen', {
        method: 'POST',
        headers: {
            'Accept': '*/*',
        },
        body: JSON.stringify({filter: {geo: {nw: nw, se: se}}}),
    })
        .then(res => res.json())
        .then(stalls => {
            placeMarkersAndInfoWindows(stalls)
        });
}

function placeMarkersAndInfoWindows(stalls) {
    stalls.forEach( function(stall) {
        const marker = new google.maps.Marker({
            position: {
                lat: stall.latitude,
                lng: stall.longitude
            },
            map,
        });

        const contentString =
            '<div class="cell-12">' +
            '<p><b>'+stall.name+'</b></p>' +
            '<div class="row" style="text-align: center">' +
            '<div class="cell-6">' +
            '<a class="button" href="'+stall.gmapsUrl+'" role="button square" style="background-color: transparent">' +
            '<img src="/assets/images/gmaps.svg"></a></div>' +
            '<div class="cell-6">' +
            '<a class="button" href="'+stall.youtubeUrl+'" role="button square" style="background-color: transparent">' +
            '<img src="/assets/images/youtube.svg"></a></div>' +
            '</div>' +
            '</div>'

        marker.addListener("click", () => {
            if (openedInfoWindow) {
                openedInfoWindow.close();
            }
            const infoWindow = new google.maps.InfoWindow({
                content: contentString,
            });
            openedInfoWindow = infoWindow;
            infoWindow.open({
                anchor: marker,
                map
            });
        });
    });
}

function initGeolocation() {
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition( (position) => {
            map.setCenter({
                lat: position.coords.latitude,
                lng: position.coords.longitude
            });
        }, () => {
            alert('Failed to get your location.')
        });
    } else {
        alert('Geolocation is not available.')
    }
}