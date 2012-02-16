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

function getCurrentIndex() {
    return myCurrentIndex;
}

function loadImgByIndexToEleId(theIndex, theEleId) {
    var aImgLocation = "building_images/" + myLocationImages[theIndex];
    document.getElementById(theEleId).innerHTML = '<img src="'+aImgLocation+'" />';
}

function processIframeContent(theIframeName, theOutputObjId, theInfoType) {
    var aIframeBody = window.frames[theIframeName].document.getElementsByTagName('body')[0];
    var aIframeTextContent = aIframeBody.textContent;
    if (aIframeTextContent === undefined) aIframeTextContent = aIframeBody.innerText;

    var aTextArray = aIframeTextContent.split("\n");
    var aInfoArray = aTextArray[getCurrentIndex()].split(": ");

    var aCurrentOutputContent = document.getElementById(theOutputObjId).innerHTML;
    document.getElementById(theOutputObjId).innerHTML = aCurrentOutputContent
    + "<br />" + theInfoType + ": " + aInfoArray[1].trim();
}

function loadInfoByIndexToEleId(theIndex, theEleId, theInfoType) {
    if ((theIndex < 0) || (theIndex >= myLocationNames.lenth)) {
        theIndex = getCurrentIndex();
    }

    var aInfoLocation = "building_info/" + theInfoType.toLowerCase() + ".txt";
    document.getElementById(theEleId).innerHTML = myLocationNames[theIndex];

    if ((theInfoType == 'Architect')
        || (theInfoType == 'Description')
        || (theInfoType == 'Year')) {
        var aIframeLoaderEle = document.getElementById('assetLoadFrame');
        aIframeLoaderEle.src = aInfoLocation;

        setTimeout(function(){
            processIframeContent('assetLoadFrame', theEleId, theInfoType);
        }, 500);
    }
}

function showInfo() {
    loadInfoByIndexToEleId(-1, 'displayWindowInfo', document.getElementById('selectInfoType').options[document.getElementById('selectInfoType').selectedIndex].value);
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
    loadIndexToEleId(getCurrentIndex() + 1, theEleId);
}

function prevIndex(theEleId) {
    loadIndexToEleId(getCurrentIndex() - 1, theEleId);
}
