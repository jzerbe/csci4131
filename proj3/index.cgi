#!/soft/perl5.8.7-bin/perl -w

use CGI qw(:standard);
use CGI::Carp qw(fatalsToBrowser);
use File::Path qw(mkpath);
use strict;

my $myCgiObj = new CGI;
my $kParamStrPhotoIndex = "pi";
my $kParamStrPhoto = "photo";
my $kParamStrText = "text";
my $kParamStrUploadPath = "MyPhotos";
my $kParamStrDbFileName = "PhotoHashDb";


### setup
mkpath($kParamStrUploadPath);


### main logic
my $aPhotoIndex = $myCgiObj->param($kParamStrPhotoIndex);
my $aPhotoFile = $myCgiObj->param($kParamStrPhoto);
my $aPhotoText = $myCgiObj->param($kParamStrText);
if ($aPhotoFile and $aPhotoText) { #process file upload

    open(UPLOADFILE, ">$kParamStrUploadPath/$aPhotoFile") or die "$!";
    while (<$aPhotoFile>) {
        print UPLOADFILE $_;
    }
    close(UPLOADFILE);

    my %myTextDbHash;
    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);
    $myTextDbHash{$aPhotoFile} = $aPhotoText;
    close(%myTextDbHash);

    print "Status: 302 Moved\nLocation: " . url(-relative=>1) . "\n\n";
} elsif (defined($aPhotoIndex) and ($aPhotoIndex ne "")) { #delete a certain photo at given index
    my %myTextDbHash;
    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);

    my $aIndexCount = 0;
    foreach my $aPhotoFileName (keys %myTextDbHash) {
        if ($aPhotoIndex == $aIndexCount) {
            unlink("$kParamStrUploadPath/$aPhotoFileName");
            delete $myTextDbHash{$aPhotoFileName};
            last;
        }
        $aIndexCount++;
    }

    close(%myTextDbHash);

    print "Status: 302 Moved\nLocation: " . url(-relative=>1) . "\n\n";
} else { #output main view
    print "Content-type: text/html\n\n";
    print qq(<!DOCTYPE html>
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
    my %myTextDbHash;
    dbmopen(%myTextDbHash, $kParamStrDbFileName, 0755);
    close(%myTextDbHash);
    #output thumbnail tiles
    my $aOutputCount = 0;
    foreach my $aPhotoFileName (keys %myTextDbHash) {
        print '<img id="aThumbImg'.$aOutputCount.'" onclick="loadIndexToEleId('.$aOutputCount
            .', &quot;displayWindow&quot;);" src="'.$kParamStrUploadPath.'/'.$aPhotoFileName
            .'" title="'.$myTextDbHash{$aPhotoFileName}.'" />';
        $aOutputCount++;
    }
    print '</div>';
    print '<div id="aTotalImageCount" style="display: none;">'.$aOutputCount.'</div>';
    my $aBaseDeletePathStr = url(-relative=>1) . "?$kParamStrPhotoIndex=";
    print qq(<div id="displayWindow">
                <button id="buttonNext" onclick="nextIndex('displayWindow');">Next</button>
                <button id="buttonPrev" onclick="prevIndex('displayWindow');">Previous</button>
                <button id="buttonClose" onclick="displayNoneByEleId('displayWindow');">Close</button>
                <button id="buttonDelete" onclick="deleteCurrentImage('$aBaseDeletePathStr');">Delete</button>
                <div class="clear">&nbsp;</div>
                <div id="displayWindowImg">&nbsp;</div>
                <div id="displayWindowInfo">&nbsp;</div>
                <div class="clear">&nbsp;</div>
            </div>
        </body>
    </html>);
    print "\n";
}
