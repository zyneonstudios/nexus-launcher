function connector(message) {
    console.log("[CONNECTOR] "+message);
}

function asyncConnector(message) {
    console.log("[CONNECTOR] async."+message);
}

document.addEventListener('contextmenu',function(e){
    e.preventDefault();
});

document.addEventListener('dragstart', function(e){
    e.preventDefault();
});