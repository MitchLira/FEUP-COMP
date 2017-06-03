const remote = require('electron').remote;
const main = remote.require('./main.js');

$(function() {
    $("#tab1").load("quickTesting.html", function() {
        
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
            var output = escapeSpaces($("#toPath").html());
            main.runCFlow(input, output, 'p1.pt');
        });
    });

    $("#tab3").load("results.html", function() {
        $("#tab3").click(() => {
            // provide data in the DOT language
            var DOTstring = 'dinetwork {1 -> 1 -> 2; 2 -> 3; 2 -- 4; 2 -> 1 }';
            var parsedData = vis.network.convertDot(DOTstring);

            var data = {
                nodes: parsedData.nodes,
                edges: parsedData.edges
            }

            var options = {
                autoResize: true,
                height: '100%',
                width: '100%',
                locale: 'en',
                clickToUse: false,
                edges: {
                    arrows: {
                        to: { enabled: true, scaleFactor: 1, type: 'arrow' },
                        middle: { enabled: false, scaleFactor: 1, type: 'arrow' },
                        from: { enabled: false, scaleFactor: 1, type: 'arrow' }
                    },
                    selectionWidth: 1,
                    smooth: {
                        enabled: false
                    }
                },
                layout: {
                    randomSeed: undefined,
                    hierarchical: {
                        improvedLayout: true,
                        enabled: true,
                        levelSeparation: 150,
                        nodeSpacing: 100,
                        treeSpacing: 200,
                        blockShifting: true,
                        edgeMinimization: true,
                        parentCentralization: true,
                        direction: 'UD',        // UD, DU, LR, RL
                        sortMethod: 'directed'   // hubsize, directed
                    }
                },
                nodes: {
                    shape: 'box',
                    
                    color: {
                        border: '#cccccc',
                        background: '#cccccc',
                        highlight: {
                            border: '#cccccc',
                            background: '#cccccc'
                        },
                        hover: {
                            border: '#cccccc',
                            background: '#cccccc'
                        }
                    },
                    font: {
                        color: 'white'
                    }
                },
                interaction: {
                    selectConnectedEdges: false
                },
                groups: {
                    criticalPath: { color: { background: 'red' }, borderWidth: 3 }
                },
                physics: {
                    enabled: false,
                }
            };

            // create a network
            var container = document.getElementById('network');
            var network = new vis.Network(container, data, options);
        });
    });
});



function escapeSpaces(x) {
    var words = x.split(" ");
    for (var i = 0; i < words.length - 1; i++) {
        words[i] += '\\';
    }
    x = words.join(" ");
    return x;
}