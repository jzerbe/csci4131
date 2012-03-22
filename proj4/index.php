<?php
// page constants
$kRequestParamStrId = "id";
$kRequestParamStrCategory = "category";
$kRequestParamStrComment = "comment";
$kRequestParamStrData = "data";
$kRequestParamStrDelete = "delete";

// connect to DBMS
require("dbms_config.php");
$myDbmsConn = mysql_connect($kDbmsHostName, $kDbmsDbUserName, $kDbmsDbUserPass) or die(mysql_error());
mysql_select_db($kDbmsDbName, $myDbmsConn) or die(mysql_error());

// setup Tables if not already exist
$aSqlCreatePhotosTable = "CREATE TABLE IF NOT EXISTS `$kTablePhotosStr` ("
        . " `$kTablePhotosFieldIdStr` INT( 5 ) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY ,"
        . " `$kTablePhotosFieldCategoryStr` TEXT NOT NULL ,"
        . " `$kTablePhotosFieldCommentStr` TEXT NOT NULL ,"
        . " `$kTablePhotosFieldDataStr` LONGBLOB NOT NULL ,"
        . " `$kTablePhotosFieldMimeTypeStr` TEXT NOT NULL"
        . " ) ENGINE = MYISAM";
mysql_query($aSqlCreatePhotosTable, $myDbmsConn);
$aSqlCreateUsersTable = "CREATE TABLE IF NOT EXISTS `$kTableUsersStr` ("
        . " `$kTableUsersFieldIdStr` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY ,"
        . " `$kTableUsersFieldUserNameStr` TEXT NOT NULL ,"
        . " `$kTableUsersFieldLoginHashStr` TEXT NOT NULL"
        . " ) ENGINE = MYISAM";
mysql_query($aSqlCreateUsersTable, $myDbmsConn);


## MAIN LOGIC ###
if (isset($_POST[$kRequestParamStrCategory]) && isset($_POST[$kRequestParamStrComment])
        && isset($_FILES[$kRequestParamStrData])) { // store image
} elseif (isset($_POST[$kRequestParamStrDelete])) { // delete image
    $aImageIdEscaped = mysql_real_escape_string($aImageId, $myDbmsConn);
    $aSqlDeletePhoto = "DELETE FROM $kTablePhotosStr WHERE $kTablePhotosFieldIdStr='$aImageIdEscaped' LIMIT 1";
    mysql_query($aSqlDeletePhoto) or die(mysql_error());
    $aSqlOptimizePhotos = "OPTIMIZE TABLE $kTablePhotosStr";
    mysql_query($aSqlOptimizePhotos) or die(mysql_error());
} elseif (isset($_POST[$kRequestParamStrId])) { // display image
    $aImageId = $_POST[$kRequestParamStrId];
    if ($aImageId <= 0) {
        die('invalid Image Id');
    } else {
        $aImageIdEscaped = mysql_real_escape_string($aImageId, $myDbmsConn);
        $aSqlFetchPhotoData = "SELECT $kTablePhotosFieldDataStr, "
                . "$kTablePhotosFieldMimeTypeStr FROM $kTablePhotosStr WHERE "
                . "$kTablePhotosFieldIdStr='$aImageIdEscaped' LIMIT 1";
        $aResultPhotoData = mysql_query($aSqlFetchPhotoData, $myDbmsConn) or die(mysql_error());
        $aPhotoData = mysql_fetch_assoc($aResultPhotoData) or die(mysql_error());
        header("Content-Type: " . $aPhotoData[$kTablePhotosFieldMimeTypeStr]);
        echo $aPhotoData[$kTablePhotosFieldDataStr];
    }
} else { // display main UI
    ?>
    <!DOCTYPE html>
    <html>
        <head>
            <title>photo gallery</title>
            <link rel="shortcut icon" id="favicon" type="image/x-icon" href="favicon.ico" />
            <meta name="author" content="Jason Zerbe" />
            <meta name="robots" content="noindex,nofollow" />
            <meta name="description" content="simple PHP/MySQL and JavaScript photo gallery" />
            <link rel="stylesheet" href="style.css" />
            <script type="text/javascript" src="script.js"></script>
        </head>
        <body>
            <form id="formUpload" method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>"
                  enctype="multipart/form-data" onsubmit="validateFileType('formUploadInputFile');">
                <strong>Add Photo</strong> <button onclick="displayNoneByEleId('formUpload');">Close</button><br /><br />
                <input id="formUploadInputFile" type="file" name="<?php echo $kRequestParamStrData; ?>"
                       onblur="validateFileType('formUploadInputFile');" /><br />
                <input type="text" name="<?php echo $kRequestParamStrComment; ?>" placeholder="photo comment" />
                <input type="submit" value="Add" />
            </form>
            <button onclick="displayBlockByEleId('formUpload');">Add Photo</button>
            <button onclick="showStart('displayWindow');">Start Show</button>
            <button onclick="showStop('displayWindow');">Stop Show</button>

            <div id="divThumbTiles">
                <?php
                $aSqlFetchPhotos = "SELECT ";
                $result = mysql_query($query);

                $myOutputCount = 0;
                while ($myOutputCount < sizeof(array('test'))) {
                    print '<img id="aThumbImg' . $i . '" onclick="loadIndexToEleId(' . $myOutputCount
                            . ', &quot;displayWindow&quot;);" src="' . $kParamStrUploadPath . '/' . $aPhotoFileName
                            . '" title="' . $myTextDbHash{$aPhotoFileName} . '" />';
                    $myOutputCount++;
                }
                ?>
            </div>
            <div id="aTotalImageCount" style="display: none;"><?php echo $myOutputCount; ?></div>
            <?php $aBaseDeletePathStr = $_SERVER['PHP_SELF'] . "?$kRequestParamStrDelete="; ?>
            <div id="displayWindow">
                <button id="buttonNext" onclick="nextIndex('displayWindow');">Next</button>
                <button id="buttonPrev" onclick="prevIndex('displayWindow');">Previous</button>
                <button id="buttonClose" onclick="displayNoneByEleId('displayWindow');">Close</button>
                <button id="buttonDelete" onclick="deleteCurrentImage('<?php echo $aBaseDeletePathStr; ?>');">Delete</button>
                <div class="clear">&nbsp;</div>
                <div id="displayWindowImg">&nbsp;</div>
                <div id="displayWindowInfo">&nbsp;</div>
                <div class="clear">&nbsp;</div>
            </div>
        </body>
    </html>
    <?php
}


// close up the DBMS connection for perf
mysql_close($myDbmsConn);
?>
