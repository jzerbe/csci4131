var myCurrentIndex = 0;
var myLocationNames = new Array("Armory", "Pillsbury Hall", "Folwell Hall",
    "Jones Hall", "Pillsbury Statue", "Wesbrook Hall", "Nicholson Hall",
    "Eddy Hall", "Music Education", "Wulling Hall");
var myLocationImages = new Array("armory.jpg", "pillsbury.jpg", "folwell.jpg",
    "jones.jpg", "statue.jpg", "wesbrook.jpg", "nicholson.jpg", "eddy.jpg",
    "music.jpg", "wulling.jpg");

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, "");
};

function displayBlockByEleId(theEleId) {
    document.getElementById(theEleId).style.display = 'block';
}

function displayNoneByEleId(theEleId) {
    document.getElementById(theEleId).style.display = 'none';
}

function toggleDisplayByEleId(theEleId) {
    if (document.getElementById(theEleId).style.display === 'none') {
        document.getElementById(theEleId).style.display = 'block';
    } else {
        document.getElementById(theEleId).style.display = 'none';
    }
}

function loadImgByIndexToEleId(theIndex, theEleId) {
    var aImgLocation = "building_images/" + myLocationImages[theIndex];
    document.getElementById(theEleId).innerHTML = '<img src="'+aImgLocation+'" />';
}

function loadTextFileToArray(theInfoLocation) {
    var xhr;
    try {
        xhr = new ActiveXObject('Msxml2.XMLHTTP');
    } catch (e) {
        try {
            xhr = new ActiveXObject('Microsoft.XMLHTTP');
        } catch (e2) {
            try {
                xhr = new XMLHttpRequest();
            } catch (e3) {
                xhr = false;
            }
        }
    }

    xhr.onreadystatechange = function() {
        if(xhr.readyState == 4) {
            if(xhr.status == 200) {
                return xhr.responseText.split("\\r?\\n");
            }else {
                return new Array();
            }
        } else {
            return new Array();
        }
    };

    xhr.open("GET", theInfoLocation,  false);
    xhr.send(null);
}

function loadInfoByIndexToEleId(theIndex, theEleId, theInfoType) {
    if ((theIndex < 0) || (theIndex >= myLocationNames.lenth)) {
        theIndex = myCurrentIndex;
    }

    var aInfoLocation = "building_info/" + theInfoType.toLowerCase() + ".txt";
    var aOutputHTML = myLocationNames[theIndex];

    if ((theInfoType == 'Architect')
        || (theInfoType == 'Description')
        || (theInfoType == 'Year')) {
        aOutputHTML = aOutputHTML + '<br />'
        + theInfoType + ': '
        + loadTextFileToArray(aInfoLocation)[theIndex].trim();
    }

    document.getElementById(theEleId).innerHTML = aOutputHTML;
}

function getSelectValueByEleId(theEleId) {
    return document.getElementById(theEleId).options[document.getElementById(theEleId).selectedIndex].value;
}

function loadIndexToEleId(theIndex, theEleId) {
    if (theIndex < 0) {
        theIndex = myLocationNames.length - 1;
    } else if (theIndex >= myLocationNames.length) {
        theIndex = 0;
    }

    myCurrentIndex = theIndex;
    loadImgByIndexToEleId(theIndex, theEleId+'Img');
    loadInfoByIndexToEleId(theIndex, theEleId+'Info',
        getSelectValueByEleId('selectInfoType'));
    displayBlockByEleId(theEleId);
}

function nextIndex(theEleId) {
    loadIndexToEleId(myCurrentIndex + 1, theEleId);
}

function prevIndex(theEleId) {
    loadIndexToEleId(myCurrentIndex - 1, theEleId);
}
