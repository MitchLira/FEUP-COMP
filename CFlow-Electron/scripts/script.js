const remote = require('electron').remote;
const main = remote.require('./main.js');

var input, output;
var availableTerms = [];

$(function() {
    $("#tab1").load("quickTesting.html", function() {
        $('.executionDiv').attr("style","display:none;");

        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/tomorrow");
        editor.getSession().setMode("ace/mode/java");	

        input = main.rootdirname + '/cflow/tmp_files/main';
        output = main.rootdirname + '/cflow/tmp_files/out';
        var classname, filename;
        
        $("#quickTest_Submit").click(function(e){
            var regex = $("#tab1 .regex").val();
            main.updateRegex(regex);
            main.runCFlow(input,output, classname);
        });

        $("#tab1 .confirm").click(() => {
            handleSuggestions('#tab1');

            main.fs.emptyDirSync(input);
            main.fs.emptyDirSync(output);

            classname = /.* class ([\w]*)/.exec(editor.getValue())[1];
            filename =  classname + ".java";

            main.fs.writeFileSync(input + '/' + filename, editor.getValue());
            prepareCFlow(input, output);

            $('.executionDiv').attr("style","display:block;");

        });
    });


    $("#tab2").load("projectTesting.html", function() {

        $('.executionDiv').attr("style","display:none;");
        
        $(".uploadFolder").click(function(e) {
            var path = main.uploadFolder();
            $(this).next(".folderPath").html(path);
            e.preventDefault();
        });

        $("#tab2 .SubmitButton").click(() => {
            var regex = $("#tab2 .regex").val();
            main.updateRegex(regex);
            main.runCFlow(input,output, $("input.runConfig").val());

            return false;
        });

        $("#tab2 .confirm").click(() => {
            handleSuggestions('#tab2');

            input = escapeSpaces($("#fromPath").html());
            output = escapeSpaces($("#toPath").html());


            var identifiersPath = output + '/identifiers.txt';
            if(main.fs.existsSync(identifiersPath))
                main.fs.unlinkSync(identifiersPath);
    
            
            //show loader
            //$( "<div class='loader'></div>" ).insertAfter( "#projectSettings" );
            
            prepareCFlow(input, output);
            $('.executionDiv').attr("style","display:block;");

        });
    });

    

    $("#tab3").load("results.html", function() {
       $("a[href='#tab3']").click(() => {
            var statisticsPath=output + "/bin/statistics";
            var nfaPath=output + "/bin/nfa";
            var dfaPath=output + "/bin/dfa";


            if(main.fs.existsSync(statisticsPath)){
                var statistics =  JSON.parse(main.fs.readFileSync(statisticsPath));
                console.log(statistics);
                
                var alert;
                if(statistics.result)
                    alert = "<div class='alert alert-success'> <strong>Regex accepted!</strong> </div>"
                else
                    alert = "<div class='alert alert-danger'> <strong>Regex not accepted!</strong> </div>"

                $(".acception").html("");
                $(".acception").append(alert);

                $("#parserStatistics").html("");
                $("#parserStatistics").append(statistics.tree);
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