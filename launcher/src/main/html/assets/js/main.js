addEventListener("DOMContentLoaded", () => {
   document.getElementById("home-button").remove();
   show("java")
});

let active;
function show(content) {
    if(document.getElementById(active)) {
        if(document.getElementById(active).classList.contains("active")) {
            document.getElementById(active).classList.remove("active");
        }
    }
    if(document.getElementById(active+"-button")) {
        if(document.getElementById(active+"-button").classList.contains("active")) {
            document.getElementById(active+"-button").classList.remove("active");
        }
    }
    document.getElementById(content+"-button").classList.add("active");
    document.getElementById(content).classList.add("active");
    active = content;
}