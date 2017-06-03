const electron = require('electron');
const {app, BrowserWindow} = electron;
const dialog = electron.dialog;
const spawn = require('child_process').spawn;
const fs = require('fs');


app.on('ready', () => {
    const {width, height} = electron.screen.getPrimaryDisplay().workAreaSize;
    let window = new BrowserWindow({width, height});
    window.loadURL(`file://${__dirname}/pages/main.html`);
});


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
        $pragma.insert after %{utils.Utils.dfa.transition("[[$pragma.content]]");}%;
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

exports.generateCode = function(input, output) {
    const ls = spawn('sh', ['cflow/bash/generateCode.sh', input, output]);

    ls.stdout.on('data', (data) => {
        console.log(`stdout: ${data}`);
    });

    ls.stderr.on('data', (data) => {
        console.log(`stderr: ${data}`);
    });

    ls.on('close', (code) => {
        console.log(`child process exited with code ${code}`);
    });
}


exports.runCFlow = function(input, output, command) {
    const sh = spawn('sh', ['cflow/bash/generateCode.sh', input, output]);

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
    const sh = spawn('sh', ['cflow/bash/runCFlow.sh', path, command]);

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