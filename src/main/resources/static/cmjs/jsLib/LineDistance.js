var LinDistance = {};
LinDistance.init = function(map){
    map.off('click');
	map.on('click', onClick);    //点击地图
	map.off('contextmenu');
	map.on('contextmenu', cancle); // 右键地图
	var markers=[];// 标记图标
	var points = [];
	var tempLine  = new L.polyline([],{dashArray: 12});
	var lines  = new L.polyline(points);
	var firstFlag = true;
	

	function onClick(e) {
		var marker;
		// 如果是第一个图标
		if(firstFlag){
			for(var item of markers){
				item.remove();
			}
			points = [];
			lines.remove();
			lines  = new L.polyline(points);
			tempLine  = new L.polyline([],{dashArray: 12});
			markers = [];
			marker = L.marker(e.latlng).bindPopup(JSON.stringify(e.latlng)).bindTooltip('start',{permanent: true});
			firstFlag = false;
			map.on('mousemove', onMove);//鼠标移动
		}else{
			marker = L.marker(e.latlng).bindPopup(JSON.stringify(e.latlng)).bindTooltip('end',{permanent: true});
			map.off('mousemove');//鼠标移动关闭
			// 测距
			var latlng = L.latLng(points[0]);
			var distance = latlng.distanceTo(e.latlng);
			ls = [points[points.length - 1], [e.latlng.lat, e.latlng.lng]]
			lines.setLatLngs(ls);
			lines.bindTooltip(distance+'米',{permanent: true});
			console.log(points)
			lines.addTo(map);
			tempLine.remove();
			firstFlag = true;
		}
		markers.push(marker);
		marker.addTo(map);
		points.push([e.latlng.lat, e.latlng.lng]);
	}

	function onMove(e) {
		if (points.length > 0) {
			ls = [points[points.length - 1], [e.latlng.lat, e.latlng.lng]];
			tempLine.setLatLngs(ls);
			map.addLayer(tempLine);
		}
	}

	// 右键取消
	function cancle(e){
		for(var marker of markers){
			marker.remove();
		}
		firstFlag = true;
		points = [];
		markers=[];
		lines .remove();
		tempLine.remove();
		lines  = new L.polyline(points);
		tempLine = new L.polyline([],{dashArray: 12});
	}
}