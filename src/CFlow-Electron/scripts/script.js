const remote = require('electron').remote;
const main = remote.require('./main.js');
const spawn = require('child_process').spawn;

var input, output;
var availableTerms = [];

$(function() {
    $("#tab1").load("quickTesting.html", function() {
    
        $("a[href='#tab1']").click(() => {
            $('.executionDiv').attr("style","display:none;");
        });

        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/tomorrow");
        editor.getSession().setMode("ace/mode/java");	

        input = main.rootdirname + '/cflow/tmp_files/main';
        output = main.rootdirname + '/cflow/tmp_files/out';
        var classname, filename;
        
        $("#quickTest_Submit").click(function(e){
            var regex = $("#tab1 .regex").val();
            main.updateRegex(regex);
            runCFlow(1,input, output, classname);
        });

        $("#tab1 .confirm").click(() => {
            $('.executionDiv').attr("style","display:none;");
            handleSuggestions('#tab1');

            main.fs.emptyDirSync(input);
            main.fs.emptyDirSync(output);

            classname = /.* class ([\w]*)/.exec(editor.getValue())[1];
            filename =  classname + ".java";

            main.fs.writeFileSync(input + '/' + filename, editor.getValue());
         
        
            main.updateIdentifiersLara(output);
            generateCode(1,input,output);
        
        });
    });


    $("#tab2").load("projectTesting.html", function() {
        $('.executionDiv').attr("style","display:none;");
        
        $("a[href='#tab2']").click(() => {
            $('.executionDiv').attr("style","display:none;");
        });
        
        $(".uploadFolder").click(function(e) {
            var path = main.uploadFolder();
            $(this).next(".folderPath").html(path);
            e.preventDefault();
        });

        $("#tab2 .SubmitButton").click(() => {
            var regex = $("#tab2 .regex").val();
            main.updateRegex(regex);
            runCFlow(2,input, output, $("input.runConfig").val());

            return false;
        });

    
        $("#tab2 .confirm").click(() => {
            $('.executionDiv').attr("style","display:none;");
            handleSuggestions('#tab2');
        
            input = escapeSpaces($("#fromPath").html());
            output = escapeSpaces($("#toPath").html());

            var identifiersPath = output + '/identifiers.txt';
            if(main.fs.existsSync(identifiersPath))
                main.fs.unlinkSync(identifiersPath);
    
            
        
            $("#tab2 .loaderContainer").html("<div class='loader text-center'></div>" );
            
            main.updateIdentifiersLara(output);
            generateCode(2,input,output);
        });
    });




    

    $("#tab3").load("results.html", function() {
    $("a[href='#tab3']").click(() => {
            var statisticsPath=output + "/bin/statistics";
            var nfaPath=output + "/bin/nfa";
            var dfaPath=output + "/bin/dfa";


            if(main.fs.existsSync(statisticsPath)){
                var statistics =  JSON.parse(main.fs.readFileSync(statisticsPath));
            
                
                var alert;
                if(statistics.result)
                    alert = "<div class='alert alert-success'> <strong>Regex accepted!</strong> </div>"
                else
                    alert = "<div class='alert alert-danger'> <strong>Regex not accepted!</strong> </div>"

                $(".acception").html("");
                $(".acception").append(alert);

                $("#parserTree").html("");
                $("#parserTree").append(statistics.tree);
            
                $("#parserStatistics").html("");
                $("#parserStatistics").append(statistics.description);
            }

            if(main.fs.existsSync(nfaPath)){
                var nfaDot = main.fs.readFileSync(nfaPath);
                createGraph("nfa",nfaDot);
            }
            
            if(main.fs.existsSync(dfaPath)){
                var dfaDot = main.fs.readFileSync(dfaPath);
                createGraph("dfa",dfaDot);
            }
    });

    

    });
});



function handleSuggestions(selector) {
    var suggestions = [];

    $("input.regex").focusin(() => {
        if ($("input.regex").val().length == 0)
            $(selector + ' .hintTooltip').html('Available terms:<br> &emsp;' + availableTerms.join(", "));
        else
            if (suggestions.length > 0)
                $(selector + ' .hintTooltip').html('Suggestions:<br> &emsp;' + suggestions.join(", "));
            else
                $(selector + ' .hintTooltip').html('No suggestions');
        
        $(this).unbind('focusin');
    });


    $("input.regex").bind("keyup", function(e) {
        var currentpos = $(this).caret();
        var text = $(this).val();

        if (text.charAt(currentpos-1).match(/[\w]/)) {
            for (var i = currentpos; i >= 0; i--) {
                if (text.charAt(i).match(/[A-Z]/))
                    break; 
            }
            
            let currentPattern = text.substr(i, currentpos + 1);
            let regex = new RegExp('^' + currentPattern + '.*');
            suggestions = [];
            for (term of availableTerms) {
                if (term.match(regex))
                    suggestions.push(term);
            }
            
            if (suggestions.length > 0)
                $(selector + ' .hintTooltip').html('Suggestions:<br> &emsp;' + suggestions.join(", "));
            else
                $(selector + ' .hintTooltip').html('No suggestions');
        } else {
            $(selector + ' .hintTooltip').html('Available terms:<br> &emsp;' + availableTerms.join(", "));
        }
    });
}


function createGraph(id,dotString){

            // provide data in the DOT language
            var DOTstring = 'dinetwork{' + dotString + '}';
            var parsedData = vis.network.convertDot(DOTstring);

            var data = {
                nodes: parsedData.nodes,
                edges: parsedData.edges
            }

            var options = parsedData.options;

            // create a network
            var container = document.getElementById(id);
            var network = new vis.Network(container, data, options);
    
}


function generateCode(tab, input, output) {
    const sh = spawn('bash', ['cflow/bash/generateCode.sh', input, output]);
    $("#tab" + tab + " .loaderContainer").html("<div class='loader text-center'></div>" );
    $("#tab" + tab + " .confirm").attr("style","display:none;");

    sh.stdout.on('data', (data) => {
    });

    sh.stderr.on('data', (data) => {
    });

    sh.on('close', (code) => {
     if(code != 0){
            alert("An erro has occured");
            $("#tab" + tab + " .confirm").attr("style","display:block;");
            $('.loader').remove();
        }else{
            var identifiersPath = output + '/identifiers.txt';
            if (main.fs.existsSync(identifiersPath)) {
                availableTerms = main.fs.readFileSync(identifiersPath).toString().split("\n");
                availableTerms = uniq(availableTerms);
                availableTerms.pop();
            }

            $('.loader').remove();
            $("#tab" + tab + " .confirm").attr("style","display:block;");
            $('.executionDiv').attr("style","display:block;");
        }
    });
}



function runCFlow(tab,input, output, command) {
    const sh = spawn('bash', ['cflow/bash/generateCode.sh', input, output]);
    $('.executionDiv').attr("style","display:none;");
    $("#tab" + tab + " .confirm").attr("style","display:none;");
    $("#tab" + tab + " .loaderContainer").html("<div class='loader text-center'></div>" );

    sh.stdout.on('data', (data) => {
    });

    sh.stderr.on('data', (data) => {
    });

    sh.on('close', (code) => {
        console.log(`child process exited with code ${code}`);
        commands = command.split(' ');
        runCode(tab,output, commands);
    });
}

function runCode(tab,path, commands) {
    commands = ['cflow/bash/runCFlow.sh', path, ...commands];
    const sh = spawn('bash', commands);

    sh.stdout.on('data', (data) => {
    });

    sh.stderr.on('data', (data) => {
    });

    sh.on('close', (code) => {
        if(code != 0){
            alert("An error has occured")
            $("#tab" + tab + " .confirm").attr("style","display:block;");
            $('.loader').remove();
        }else{
            $("#tab" + tab + " .confirm").attr("style","display:block;");
            $('.loader').remove();
            $("a[href='#tab3']").click();
        }
    });
}



function uniq(a) {
    var seen = {};
    return a.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
}


function escapeSpaces(x) {
    var words = x.split(" ");
    for (var i = 0; i < words.length - 1; i++) {
        words[i] += '\\';
    }
    x = words.join(" ");
    return x;
}




