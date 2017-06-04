const remote = require('electron').remote;
const main = remote.require('./main.js');

var availableTerms = [];

$(function() {
    $("#tab1").load("quickTesting.html", function() {
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/tomorrow");
        editor.getSession().setMode("ace/mode/java");	

        var srcFilePath = main.rootdirname + '/cflow/tmp_files/main';
        var dstFilePath = main.rootdirname + '/cflow/tmp_files/out';
        var classname, filename;
        
        $("#quickTest_Submit").click(function(e){
            var regex = $("#tab1 .regex").val();
            main.updateRegex(regex);
            main.runCode(dstFilePath, classname);
        });

        $("#tab1 .confirm").click(() => {
            handleSuggestions();

            main.fs.emptyDirSync(srcFilePath);
            main.fs.emptyDirSync(dstFilePath);

            classname = /.* class ([\w]*)/.exec(editor.getValue())[1];
            filename =  classname + ".java";

            main.fs.writeFileSync(srcFilePath + '/' + filename, editor.getValue());
            prepareCFlow(srcFilePath, dstFilePath);
        });
    });

    $("#tab2").load("projectTesting.html", function() {
        var input, output;


        $(".uploadFolder").click(function(e) {
            var path = main.uploadFolder();
            $(this).next(".folderPath").html(path);
            e.preventDefault();
        });

        $("#tab2 .SubmitButton").click(() => {
            var regex = $("#tab2 .regex").val();
            main.updateRegex(regex);
            main.runCode(output, 'p1.pt');
        });

        $("#tab2 .confirm").click(() => {
            input = escapeSpaces($("#fromPath").html());
            output = escapeSpaces($("#toPath").html());
            main.updateIdentifiersLara(output);
            main.generateCode(output);
        });
    });

    $("#tab3").load("results.html", function() {
       $("a[href='#tab3']").click(() => {

            var nfaDot = main.fs.readFileSync(output + "/bin/nfa");
              console.log("nfa->" + nfaDot );
            createGraph("nfa",nfaDot);
            
            var dfaDot = main.fs.readFileSync(output + "/bin/dfa");
            createGraph("dfa",dfaDot);

       });

     

    });
});



function handleSuggestions() {
    var suggestions = [];

    $("input.regex").focusin(() => {
        if ($("input.regex").val().length == 0)
            $('#tab1 .hintTooltip').html('Available terms:<br> &emsp;' + availableTerms.join(", "));
        else
            if (suggestions.length > 0)
                $('#tab1 .hintTooltip').html('Suggestions:<br> &emsp;' + suggestions.join(", "));
            else
                $('#tab1 .hintTooltip').html('No suggestions');
        
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
                $('#tab1 .hintTooltip').html('Suggestions:<br> &emsp;' + suggestions.join(", "));
            else
                $('#tab1 .hintTooltip').html('No suggestions');
        } else {
            $('#tab1 .hintTooltip').html('Available terms:<br> &emsp;' + availableTerms.join(", "));
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

            var options = {
              
            };

            // create a network
            var container = document.getElementById(id);
            var network = new vis.Network(container, data, options);
     
}


function escapeSpaces(x) {
    var words = x.split(" ");
    for (var i = 0; i < words.length - 1; i++) {
        words[i] += '\\';
    }
    x = words.join(" ");
    return x;
}



function prepareCFlow(src, dst) {
    main.updateIdentifiersLara(dst);
    main.generateCode(src, dst);

    availableTerms = main.fs.readFileSync(dst + '/identifiers.txt').toString().split("\n");
    availableTerms = uniq(availableTerms);
    availableTerms.pop();
}


function uniq(a) {
    var seen = {};
    return a.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
}