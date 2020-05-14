var localhostPath;
(function () {
    // http://127.0.0.1:9090/cmjs/jsLib/emBase.js
    var srcPath = document.currentScript.src; // 获取到当前script标签的src
    var idx = srcPath.indexOf("/js/lmJs/lmBase.js");
    var contexPaht = srcPath.substring(0,idx);
    localhostPath = contexPaht;
    document.write('<meta name="viewport" content="width=device-width,initial-scale=1.0">');
    document.write('<meta http-equiv="X-UA-Compatible" content="ie=edge">');
    document.write('<link rel="stylesheet" type="text/css" href="'+localhostPath+'/js/leafletJs/leaflet.css">');
    document.write('<link rel="stylesheet" type="text/css" href="'+localhostPath+'/js/lmcss/blinkmarker.css">');

    document.write('<script type="text/javascript" src="'+localhostPath+'/js/leafletJs/leaflet-src.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/leaflet-tilelayer-colorizr.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/setMapStytle.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/initMap.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/blinkmarker.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/DrawTool.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/GeoUtil.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/LineDistance.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/markerTrack.js"></script>');
    document.write('<script type="text/javascript" src="'+localhostPath+'/js/lmJs/lm.geometryutil.js"></script>');
})();