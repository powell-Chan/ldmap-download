// 离线地图瓦片服务器地址
var url = '/img/{z}/{x}/{y}.png';
// var url = 'http://127.0.0.1:9090/img/{z}/{x}/{y}.png';
// var url = 'http://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1scale=1&style=5&{x}&{y}&{z}.png';
L.initMap = function(mapDiv,data){
	var map;
	if(data){
		map = L.map(mapDiv, {
			center: (data.center ? data.center : [34.694, 113.587]),
			renderer: L.svg(),
			zoom: (data.zoom ? data.zoom : 11),
			zoomControl: (data.zoomControl ? data.zoomControl : false), // + -号放大缩小
			attributionControl: (data.attributionControl ? data.attributionControl : false) // 右下角leaflet.js图标
		});	
	}else{
		map = L.map(mapDiv, {
			center: [34.694, 113.587],
			renderer: L.svg(),
			zoom: 6,
			zoomControl:  false, // + -号放大缩小
			attributionControl:  false // 右下角leaflet.js图标
		});
	}
	if (data.mapStyle && data.mapStyle.indexOf("blue") != -1){
		document.write('<link rel="stylesheet" type="text/css" href="'+localhostPath+'/js/lmcss/blue.css">');
	}
    
	//将图层加载到地图上，并设置最大的聚焦还有map样式
	// L.tileLayer(localhostPath+url, {
	// 	maxZoom: data.maxZoom ? data.maxZoom : 18,
	// 	minZoom: data.minZoom ? data.minZoom : 3
    // }).addTo(map);
	L.tileLayer.colorizr(localhostPath+url, {
		maxZoom: 18,
		minZoom: 3,
		colorize: function (pixel) {
			// 科技紫主题
			if (!data.mapStyle || data.mapStyle == 'default'){

			}
			if (data.mapStyle == 'bluePurpled'){ // 蓝紫色主题
				setRgbMinus(pixel,[26,34,111]);
			}
			if (data.mapStyle == 'blueBlack'){ // 深蓝色主题
				setRgbMinus(pixel,[0,0,70]);
			}
			if (data.mapStyle == 'blue'){ // 科技蓝色主题
				setRgbMinus(pixel,[15,15,110]);
			}
			if (data.mapStyle == 'fresh'){ // 鲜艳的主题色
				setRgbMinus(pixel,[13,17,90]);
			}
			return pixel;
		}
	}).addTo(map);
    return map;
}