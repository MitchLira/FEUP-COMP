const electron = require('electron');
const {app, BrowserWindow} = electron;
const dialog = electron.dialog;
const spawn = require('child_process').spawn;
const spawnSync = require('child_process').spawnSync;
const fs = require('fs-extra');


app.on('ready', () => {
    const {width, height} = electron.screen.getPrimaryDisplay().workAreaSize;
    let window = new BrowserWindow({width, height});
    window.loadURL(`file://${__dirname}/pages/main.html`);
});


exports.rootdirname = __dirname;

exports.fs = fs;

exports.uploadFolder = function() {
    return dialog.showOpenDialog({
        properties: ['openDirectory']
    });
};


exports.updateRegex = function(regex) {
    var lara = 
`aspectdef CFlow
    select pragma{"BasicBlock"} end
        apply
        $pragma.insert after  %{utils.Utils.dfa.transition("[[$pragma.content]]");}%;
        end
      

    select method{"main"}.first_stmt end
        apply
            $first_stmt.insert before %{utils.Utils.initDfa("${regex}");}%;
        end


    select method{'main'}.return end
        apply
            $return.insert before %{utils.Utils.dfaStatistics();}%;
        end
        condition
            $return != null
        end

    select method{'main'}.last_stmt end
        apply
            $last_stmt.insert after %{utils.Utils.dfaStatistics();}%;
        end
        condition
            $return == null
        end
end`;
    
    
    fs.writeFile('cflow/lara/cflow.lara', lara);
}

exports.updateIdentifiersLara = function(output) {
    var lara = 
`aspectdef PragmaPrinter
   var file = new java.io.File("${output + "/identifiers.txt"}");
   select pragma{"BasicBlock"} end
    apply
        IoUtils.append(file, $pragma.content + "\\n");
    end
end`;
    
    fs.writeFile('cflow/lara/identifiers.lara', lara);
}


exports.generateCode = function(input, output) {
    return spawnSync('bash', ['cflow/bash/generateCode.sh', input, output]);
}


exports.runCFlow = function(input, output, command) {
    const sh = spawn('bash', ['cflow/bash/generateCode.sh', input, output]);

    sh.stdout.on('data', (data) => {
        console.log(`stdout: ${data}`);
    });

    sh.stderr.on('data', (data) => {
        console.log(`stderr: ${data}`);
    });

    sh.on('close', (code) => {
        console.log(`child process exited with code ${code}`);
        exports.runCode(output, command);
    });

}

exports.runCode = function(path, command) {
    const sh = spawn('bash', ['cflow/bash/runCFlow.sh', path, command]);

    sh.stdout.on('data', (data) => {
        console.log(`stdout: ${data}`);
    });

    sh.stderr.on('data', (data) => {
        console.log(`stderr: ${data}`);
    });

    sh.on('close', (code) => {
        console.log(`child process exited with code ${code}`);
    });
}





exports.rmDir = function(dirPath) {
    try { var files = fs.readdirSync(dirPath); }
    catch(e) { return; }
    if (files.length > 0)
    for (var i = 0; i < files.length; i++) {
        var filePath = dirPath + '/' + files[i];
        if (fs.statSync(filePath).isFile())
            fs.unlinkSync(filePath);
        else
            rmDir(filePath);
    }
    fs.rmdirSync(dirPath);
};