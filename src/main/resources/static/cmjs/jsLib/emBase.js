var localhostPath;
(function () {
    // http://127.0.0.1:9090/cmjs/jsLib/emBase.js
    var srcPath = document.currentScript.src; // 获取到当前script标签的src
    var idx = srcPath.indexOf("/cmjs");
    var contexPaht = srcPath.substring(0,idx);
    localhostPath = contexPaht;
    document.write('<meta name="viewport" content="width=device-width,initial-scale=1.0">');
    document.write('<meta http-equiv="X-UA-Compatible" content="ie=edge">');
    document.write('<link rel="stylesheet" type="text/css" href="'+localhostPath+'/cmjs/dist/leaflet.css">');
    document.write('<link rel="stylesheet" type="text/css" href="'+localhostPath+'/cmjs/css/blinkmarker.css">');

    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/dist/leaflet-src.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/setMapStytle.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/leaflet-tilelayer-colorizr-gh-pages/leaflet-tilelayer-colorizr.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/initMap.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/blinkmarker.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/DrawTool.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/GeoUtil.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/LineDistance.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/markerTrack.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/cmjs/jsLib/em.geometryutil.js"></script>');
})();