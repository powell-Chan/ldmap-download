L.setMapStytle = function (pixel,dataStr) {
    var rgb = []
    if(rgbSameAs(pixel,)){

    }
}

rgbSameAs = function(pixelSrc,pixelTarget){
    if (pixelSrc.r == pixelTarget[0] && pixelSrc.g == pixelTarget[1] && pixelSrc.b == pixelTarget[2]){
        return true;
    }
    return false;
}

setRgbAs = function(pixelSrc,pixelTarget){
    pixelSrc.r = pixelTarget[0];
    pixelSrc.g = pixelTarget[1];
    pixelSrc.b = pixelTarget[2];
}

setRgbAdd = function(pixelSrc,pixelTarget){
    pixelSrc.r += pixelTarget[0];
    pixelSrc.g += pixelTarget[1];
    pixelSrc.b += pixelTarget[2];
}


setRgbMinus = function(pixelSrc,pixelTarget){
    pixelSrc.r -= pixelTarget[0];
    pixelSrc.g -= pixelTarget[1];
    pixelSrc.b -= pixelTarget[2];
}