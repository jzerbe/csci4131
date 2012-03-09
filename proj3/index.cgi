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
            <title>Photo Gallery</title>
            <meta name="robots" content="noindex,nofollow" />
            <link rel="shortcut icon" href="./favicon.ico" />
            <script type="text/javascript" src="script.js"></script>

            <style type="text/css">
                #divThumbTiles {
                    border: 1px dotted black;
                    margin: 2% 3%;
                    padding: 5px 10px;
                    width: 90%;
                }
                #formUpload {
                    background: gray;
                    border: 1px dotted black;
                    display: none;
                    left: 15%;
                    padding: 2px 5px;
                    position: absolute;
                    top: 15%;
                    width: 220px;
                    z-index: 100;
                }
            </style>
        </head>
        <body>
        );
    print '<form id="formUpload" method="post" action="' . url(-relative=>1) . '" enctype="multipart/form-data">' . "\n";
    print qq(<strong>Add Photo</strong> <button onclick="displayNoneByEleId('formUpload');">Close</button><br /><br />);
    print '<input type="file" name="' . $kParamStrPhoto . '" /><br />' . "\n";
    print '<input type="text" name="' . $kParamStrText . '" />';
    print qq(<input type="submit" value="Add" />
            </form>
            <button onclick="displayBlockByEleId('formUpload');">Add Photo</button><br />
            <div id="divThumbTiles">);

    #get image list and text from database
    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);
    close(%myTextDbHash);
    #output thumbnail tiles

    print qq(
            </div>
        </body>
    </html>
    );
}
