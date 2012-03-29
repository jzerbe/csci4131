Jason Zerbe
3830775

simple photomanager using PHP/MySQL back-end
WARNING: the cookie auth used in this bundle is very weak

setup on CSELabs webserver
1) upload all files in chmod 701 directory
2) chmod 755 index.php; chmod 755 dbms_config.php; chmod 604 favicon.ico; chmod 604 script.js; chmod 604 style.css
3) access with web-browser. login is prefilled with correct values [username, password].
index.php script populates the database with all needed schema and data at run-time if not already there.

under-the-hood information
see dbms_config.php for full list of MySQL connection constants
MySQL username = C4131S12U105
MySQL password = 13347

`Photos` table has `id` INT(5), `category` TEXT, `comment` TEXT,
`data` LONGBLOG - for storing image bytes, and `mime_type` - for image data MIME type

`Users` tables has `user_name` VARCHAR (200), and `login_hash` VARCHAR (200) -
which should in the future store SHA1 of password, but for now stores plaintext
