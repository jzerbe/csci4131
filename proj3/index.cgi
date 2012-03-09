#!/usr/bin/perl

use CGI qw(:standard);
use CGI::Carp qw(fatalsToBrowser);
use File::Path qw(mkpath);
use strict;

my $myCgiObj = new CGI;
my $kParamStrPhoto = "photo";
my $kParamStrText = "text";
my $kParamStrUploadPath = "MyPhotos";
my %myTextDbHash;
my $kParamStrDbFileName = "PhotoHashDb";


### setup
mkpath($kParamStrUploadPath);


### main logic
my $aPhotoFile = $myCgiObj->param($kParamStrPhoto);
my $aPhotoText = $myCgiObj->param($kParamStrText);
if ($aPhotoFile and $aPhotoText) { #process file upload

    open(UPLOADFILE, ">$kParamStrUploadPath/$aPhotoFile") or die "$!";
    while (<$aPhotoFile>) {
        print UPLOADFILE $_;
    }
    close(UPLOADFILE);

    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);
    $myTextDbHash{$aPhotoFile} = $aPhotoText;
    close(%myTextDbHash);

    print "Status: 302 Moved\nLocation: " . url(-relative=>1) . "\n\n";
} else { #output main view
    print qq(Content-type: text/html

    <!DOCTYPE html>
    <html>
        <head>
            <title>Simple Slide-Show</title>
            <link rel="shortcut icon" id="favicon" type="image/x-icon" href="favicon.ico" />
            <meta name="author" content="Jason Zerbe" />
            <meta name="robots" content="noindex,nofollow" />
            <meta name="description" content="simple Perl and JavaScript photo gallery" />
            <link rel="stylesheet" href="style.css" />
            <script type="text/javascript" src="script.js"></script>
        </head>
        <body>
        );
    print '<form id="formUpload" method="post" action="'.url(-relative=>1)
        .'" enctype="multipart/form-data" onsubmit="validateFileType(&quot;formUploadInputFile&quot;);">'."\n";
    print qq(<strong>Add Photo</strong> <button onclick="displayNoneByEleId('formUpload');">Close</button><br /><br />);
    print '<input id="formUploadInputFile" type="file" name="'.$kParamStrPhoto
        .'" onblur="validateFileType(&quot;formUploadInputFile&quot;);" /><br />'."\n";
    print '<input type="text" name="'.$kParamStrText.'" />';
    print qq(<input type="submit" value="Add" />
            </form>
            <button onclick="displayBlockByEleId('formUpload');">Add Photo</button>
            <button onclick="showStart('displayWindow');">Start Show</button>
            <button onclick="showStop('displayWindow');">Stop Show</button>

            <div id="divThumbTiles">);

    #get image list and text from database
    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);
    close(%myTextDbHash);
    #output thumbnail tiles
    my $aOutputCount = 0;
    foreach my $aFileName (keys %myTextDbHash) {
        print '<img id="aThumbImg'.$aOutputCount.'" onclick="loadIndexToEleId('.$aOutputCount
            .', &quot;displayWindow&quot;);" src="'.$kParamStrUploadPath.'/'.$aFileName
            .'" title="'.$myTextDbHash{$aFileName}.'" /></a>';
        $aOutputCount++;
    }
    print '</div>';
    print '<div id="aTotalImageCount" style="display: none;">'.$aOutputCount.'</div>';

    print qq(<div id="displayWindow">
                <button onclick="nextIndex('displayWindow');">Next</button>
                <button onclick="prevIndex('displayWindow');">Previous</button>
                <button onclick="displayNoneByEleId('displayWindow');">Close</button>
                <div class="clear">&nbsp;</div>
                <div id="displayWindowImg">&nbsp;</div>
                <div id="displayWindowInfo">&nbsp;</div>
                <div class="clear">&nbsp;</div>
            </div>
        </body>
    </html>
    );
}
