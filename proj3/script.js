/**
 * globally applicable variables
 */
var myCurrentIndex = 0;
function getCurrentIndex() {
    return myCurrentIndex;
}
function getMaxIndex() {
    return (document.getElementById('aTotalImageCount').innerHTML - 1);
}

String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, "");
};


/**
 * utility functions
 */
function displayBlockByEleId(theEleId) {
    document.getElementById(theEleId).style.display = 'block';
}

function displayNoneByEleId(theEleId) {
    showStop();
    setThumbSelectedByIndex(-1);
    document.getElementById(theEleId).style.display = 'none';
}

function toggleDisplayByEleId(theEleId) {
    if (document.getElementById(theEleId).style.display === 'none') {
        document.getElementById(theEleId).style.display = 'block';
    } else {
        document.getElementById(theEleId).style.display = 'none';
    }
}


/**
 * validation methods
*/
function validateFileType(theEleId) {
    var aFileInputValue = document.getElementById(theEleId).value;

    if(aFileInputValue != '') {
        var aValidExtensionRegex = /(.jpg|.jpeg|.gif|.bmp|.png)$/i;

        if(!aValidExtensionRegex.test(aFileInputValue)) {
            alert('Invalid File');
            document.getElementById(theEleId).parentNode.reset();
            return false;
        }
    }

    return true;
}


/**
 * functions for populating DOM with dynamically loaded content
 */
var kThumbImgPrefixStr = 'aThumbImg';

function setThumbSelectedByIndex(theIndex) {
    for (var i = 0; i <= getMaxIndex(); i++) {
        document.getElementById(kThumbImgPrefixStr+i).style.border = 'none';
    }
    if ((0 <= theIndex) && (theIndex <= getMaxIndex())) {
        document.getElementById(kThumbImgPrefixStr+theIndex).style.border = '2px solid red';
    }
}

function loadIndexToEleId(theIndex, theEleId) {
    if (theIndex < 0) {
        theIndex = getMaxIndex();
    } else if (theIndex > getMaxIndex()) {
        theIndex = 0;
    }
    myCurrentIndex = theIndex;

    setThumbSelectedByIndex(theIndex);

    document.getElementById(theEleId+'Img').innerHTML = '<img src="' + document.getElementById(kThumbImgPrefixStr+theIndex).src + '" />';
    document.getElementById(theEleId+'Info').innerHTML = document.getElementById(kThumbImgPrefixStr+theIndex).title;

    displayBlockByEleId(theEleId);
}

function nextIndex(theEleId) {
    loadIndexToEleId(getCurrentIndex() + 1, theEleId);
}

function prevIndex(theEleId) {
    loadIndexToEleId(getCurrentIndex() - 1, theEleId);
}


/**
 * slide-show timed functions
 */
var mySlideShowIsRunning = false;
function isSlideShowRunning() {
    return mySlideShowIsRunning;
}

var myTimeOutObj;
function showRun(theEleId) {
    nextIndex(theEleId);
    myTimeOutObj = setTimeout(function(){
        showRun(theEleId);
    }, 2000);
}
function showStart(theEleId) {
    if (isSlideShowRunning()) {
        alert("The slide show is already running!");
    } else {
        mySlideShowIsRunning = true;
        myCurrentIndex = myCurrentIndex - 1;
        showRun(theEleId);
    }
}
function showStop() {
    clearTimeout(myTimeOutObj);
    mySlideShowIsRunning = false;
}
