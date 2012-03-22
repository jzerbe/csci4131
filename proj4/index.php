<?php
/**
 * mysql_escape_string is deprecated as of PHP 5.3.0 and should be
 * replaced with mysql_real_escape_string in the future
 */
// page constants
$kRequestParamStrId = "id";
$kRequestParamStrCategory = "category";
$kCategoryStrArray = array('work', 'friends', 'sports', 'nature', 'school');
$kRequestParamStrComment = "comment";
$kRequestParamStrData = "data";
$kRequestParamStrDelete = "delete";

// cookie constants - see README for weak auth warning
$kCookieUserNameStr = "pg_username";
$kCookieLoginHashStr = "pg_loginhash";

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
    if ($_FILES[$kRequestParamStrData]['error'] == 0) {
        $category = mysql_escape_string($_POST[$kRequestParamStrCategory]);
        $comment = mysql_escape_string($_POST[$kRequestParamStrComment]);
        $data = mysql_escape_string(file_get_contents($_FILES[$kRequestParamStrData]['tmp_name']));
        $mime_type = mysql_escape_string($_FILES[$kRequestParamStrData]['type']);

        $aSqlInsertPhotoTuple = "INSERT INTO `$kTablePhotosStr` (`$kTablePhotosFieldCategoryStr`,"
                . " `$kTablePhotosFieldCommentStr`, `$kTablePhotosFieldDataStr`,"
                . " `$kTablePhotosFieldMimeTypeStr`) VALUES"
                . " ('{$category}', '{$comment}', '{$data}', '{$mime_type}')";
        mysql_query($aSqlInsertPhotoTuple, $myDbmsConn) or die(mysql_error());
    }

    header("Location: " . $_SERVER['PHP_SELF']);
} elseif (isset($_GET[$kRequestParamStrDelete])) { // delete image
    $aImageId = $_GET[$kRequestParamStrDelete];
    $aImageId = $aImageId + 1;
    if ($aImageId <= 0) {
        die('invalid Image Id');
    } else {
        $aImageIdEscaped = mysql_escape_string($aImageId);
        $aSqlDeletePhoto = "DELETE FROM $kTablePhotosStr WHERE $kTablePhotosFieldIdStr='$aImageIdEscaped' LIMIT 1";
        mysql_query($aSqlDeletePhoto) or die(mysql_error());
        $aSqlOptimizePhotos = "OPTIMIZE TABLE $kTablePhotosStr";
        mysql_query($aSqlOptimizePhotos) or die(mysql_error());
    }

    header("Location: " . $_SERVER['PHP_SELF']);
} elseif (isset($_GET[$kRequestParamStrId])) { // display image
    $aImageId = $_GET[$kRequestParamStrId];
    $aImageId = $aImageId + 1;
    if ($aImageId <= 0) {
        die('invalid Image Id');
    } else {
        $aImageIdEscaped = mysql_escape_string($aImageId);
        $aSqlFetchPhotoData = "SELECT $kTablePhotosFieldDataStr, "
                . "$kTablePhotosFieldMimeTypeStr FROM $kTablePhotosStr WHERE "
                . "$kTablePhotosFieldIdStr='$aImageIdEscaped' LIMIT 1";
        $aResultPhotoData = mysql_query($aSqlFetchPhotoData, $myDbmsConn) or die(mysql_error());
        $aPhotoData = mysql_fetch_assoc($aResultPhotoData) or die(mysql_error());
        header("Content-Type: " . $aPhotoData[$kTablePhotosFieldMimeTypeStr]);
        echo $aPhotoData[$kTablePhotosFieldDataStr];
    }
} else if (isset($_POST[$kCookieUserNameStr]) && isset($_POST[$kCookieLoginHashStr])) {
    $aExpiryTime = time() + 60 * 3; // 3 minute expiry since set
    setcookie($kCookieUserNameStr, $_POST[$kCookieUserNameStr], $aExpiryTime);
    setcookie($kCookieLoginHashStr, $_POST[$kCookieLoginHashStr], $aExpiryTime);

    header("Location: " . $_SERVER['PHP_SELF']);
} else if (!isset($_COOKIE[$kCookieUserNameStr]) || !isset($_COOKIE[$kCookieLoginHashStr])) { // need to auth
    ?>
    <!DOCTYPE html>
    <html>
        <head>
            <title>photo gallery</title>
            <link rel="shortcut icon" id="favicon" type="image/x-icon" href="favicon.ico" />
            <meta name="author" content="Jason Zerbe" />
            <meta name="robots" content="noindex,nofollow" />
            <meta name="description" content="simple PHP-MySQL and JavaScript photo gallery" />
        </head>
        <body>
            <form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
                <input type="text" name="<?php echo $kCookieUserNameStr; ?>" value="username" />
                <input type="password" name="<?php echo $kCookieLoginHashStr; ?>" value="password" />
                <input type="submit" value="Login" />
            </form>
        </body>
    </html>
    <?php
} else { // display main UI
    ?>
    <!DOCTYPE html>
    <html>
        <head>
            <title>photo gallery</title>
            <link rel="shortcut icon" id="favicon" type="image/x-icon" href="favicon.ico" />
            <meta name="author" content="Jason Zerbe" />
            <meta name="robots" content="noindex,nofollow" />
            <meta name="description" content="simple PHP-MySQL and JavaScript photo gallery" />
            <link rel="stylesheet" href="style.css" />
            <script type="text/javascript" src="script.js"></script>
        </head>
        <body>
            <form id="formUpload" method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>"
                  enctype="multipart/form-data" onsubmit="validateFileType('formUploadInputFile');">
                <strong>Add Photo</strong> <button onclick="displayNoneByEleId('formUpload');">Close</button><br /><br />
                <input id="formUploadInputFile" type="file" name="<?php echo $kRequestParamStrData; ?>"
                       onblur="validateFileType('formUploadInputFile');" /><br />
                <select name="<?php echo $kRequestParamStrCategory; ?>">
                    <?php
                    foreach ($kCategoryStrArray as $kCategoryStr) {
                        echo "<option>$kCategoryStr</option>";
                    }
                    ?>
                </select><br />
                <input type="text" name="<?php echo $kRequestParamStrComment; ?>" placeholder="photo comment" />
                <input type="submit" value="Add" />
            </form>
            <button onclick="displayBlockByEleId('formUpload');">Add Photo</button>
            <button onclick="showStart('displayWindow');">Start Show</button>
            <button onclick="showStop('displayWindow');">Stop Show</button>

            <div id="divThumbTiles">
                <?php
                $aSqlFetchPhotoData = "SELECT $kTablePhotosFieldIdStr, $kTablePhotosFieldCategoryStr,"
                        . " $kTablePhotosFieldCommentStr FROM $kTablePhotosStr";
                $aPhotosQueryResult = mysql_query($aSqlFetchPhotoData) or die(mysql_error());

                $myOutputCount = 0;
                while ($row = mysql_fetch_assoc($aPhotosQueryResult)) {
                    echo '<img id="aThumbImg' . ($row[$kTablePhotosFieldIdStr] - 1) . '" onclick="loadIndexToEleId('
                    . ($row[$kTablePhotosFieldIdStr] - 1) . ', &quot;displayWindow&quot;);" src="'
                    . $_SERVER['PHP_SELF'] . '?' . $kRequestParamStrId
                    . '=' . ($row[$kTablePhotosFieldIdStr] - 1) . '" title="'
                    . $row[$kTablePhotosFieldCategoryStr] . ' : ' . $row[$kTablePhotosFieldCommentStr] . '" />';

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
