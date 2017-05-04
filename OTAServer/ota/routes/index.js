var express = require('express');
var router = express.Router();

var fs = require('fs');
var path = require('path');
var crypto = require('crypto');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/update_info', function(req, res, next) {
    var name = req.query.versionName;
    console.log('current version:' + name);

    var info = {
        'url': '/ota_file/patch.zip',
        'updateMessage': 'Fix bugs.',
        'versionName': 'v2',
        'md5': '',
        'diffUpdate': true                        
	};

    var dir = process.cwd() + '/ota_file'
    var filePath = path.join(dir, 'update.zip');
    var md5 = getFileMD5(filePath);
    info.md5 = md5;

    res.writeHead(200, {'Content-Type': 'application/json;charset=utf-8'});
  	res.end(JSON.stringify(info));
});

router.get('/ota_file/:filename', function(req, res, next) {
    var filename = req.params.filename;
    var dir = process.cwd() + '/ota_file'
    var filePath = path.join(dir, filename);

    fs.exists(filePath, function(exist) {
        if (exist) {
            console.log('downloading:' + filename);
            res.download(filePath); 
        }
        else {
            res.set('Content-type', 'text/html');
            res.end('File not exist.');
        }
    });
});

function getFileMD5(filePath) {
    var buffer = fs.readFileSync(filePath);
    var fsHash = crypto.createHash('md5');

    fsHash.update(buffer);
    var md5 = fsHash.digest('hex');
    console.log("文件的MD5是：%s", md5);

    return md5;
}

module.exports = router;
