const remote = require('electron').remote;
const main = remote.require('./main.js');
var output;


$(function() {
   

    $("#tab1").load("quickTesting.html", function() {
        var editor = ace.edit("editor");
        editor.setTheme("ace/theme/tomorrow");
        editor.getSession().setMode("ace/mode/java");	
        
        $("#quickTest_Submit").click(function(e){
            var regex = $("#tab1 .regex").val();
            main.updateRegex(regex);
            main.updateIdentifiersLara('cflow/tmp_files/out');

            var classname = /.* class ([\w]*)/.exec(editor.getValue())[1];
            var filename =  classname + ".java";
            var srcFilePath = main.rootdirname + '/cflow/tmp_files/main';
            var dstFilePath = main.rootdirname + '/cflow/tmp_files/out';

            main.fs.writeFileSync(srcFilePath + '/' + filename, editor.getValue());
            main.runCFlow(srcFilePath, dstFilePath, classname);
        });



    });

    $("#tab2").load("projectTesting.html", function() {
        $(".uploadFolder").click(function(e) {
            var path = main.uploadFolder();
            $(this).next(".folderPath").html(path);
            e.preventDefault();
        });

        $("#tab2 .SubmitButton").click(() => {
            var regex = $("#tab2 .regex").val();
            main.updateRegex(regex);
         

            var input = escapeSpaces($("#fromPath").html());
            output = escapeSpaces($("#toPath").html());
            main.updateIdentifiersLara(output);
               
            main.runCFlow(input, output, 'p1.pt');
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